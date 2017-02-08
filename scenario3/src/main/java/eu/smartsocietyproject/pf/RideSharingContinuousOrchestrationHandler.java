package eu.smartsocietyproject.pf;

import at.ac.tuwien.dsg.smartcom.exception.CommunicationException;
import at.ac.tuwien.dsg.smartcom.model.Identifier;
import at.ac.tuwien.dsg.smartcom.model.Message;
import eu.smartsocietyproject.pf.cbthandlers.CBTLifecycleException;
import eu.smartsocietyproject.pf.cbthandlers.ContinuousOrchestrationHandler;
import eu.smartsocietyproject.scenario3.S3Application;
import eu.smartsocietyproject.scenario3.S3Plan;
import eu.smartsocietyproject.scenario3.S3TaskRequest;
import eu.smartsocietyproject.smartcom.SmartComService;
import eu.smartsocietyproject.utils.RideSharingAPI;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class RideSharingContinuousOrchestrationHandler implements ContinuousOrchestrationHandler {

    private final RideSharingAPI api;
    private final long pollingTime;

    public RideSharingContinuousOrchestrationHandler(RideSharingAPI api, long pollingTime) {
        this.api = api;
        this.pollingTime = pollingTime;
    }

    @Override
    public CollectiveWithPlan continuousOrchestration(ApplicationContext context, TaskRequest t)
        throws CBTLifecycleException {
        S3TaskRequest request = (S3TaskRequest) t;
        Optional<String> requestId = api.sendRequest(request);

        if ( !requestId.isPresent() ) {
            throw new CBTLifecycleException("Unable to send request");
        }
        sendRequestConfirmation(context.getSmartCom(), request, requestId.get());

        String planId =
            api.waitForPlan(request, requestId.get(), pollingTime)
               .orElseThrow(() -> new CBTLifecycleException(
                   "Unable to get a plan for the request, " +
                       "either the request was unsuccessful or the deadline expired"));
        try {
            return CollectiveWithPlan.of(
                ApplicationBasedCollective.empty(context, UUID.randomUUID().toString(), S3Application.EMPTYCOLLECTIVE),
                new S3Plan(planId, request));
        } catch (Collective.CollectiveCreationException e) {
            throw new CBTLifecycleException(e);
        }
    }

    private void sendRequestConfirmation(SmartComService service, S3TaskRequest request, String requestId)
        throws CBTLifecycleException {
        Message m =
            new Message.MessageBuilder()
                .setConversationId(request.getConversation())
                .setReceiverId(Identifier.peer(request.getPeer()))
                .setType("Scenario3")
                .setSubtype("RequestSubmitted")
                .setContent(requestId)
                .create();
        try {
            service.send(m);
        } catch (CommunicationException e) {
            throw new CBTLifecycleException(
                String.format("Unable to communicate request submission ack for request %s to the peer %s",
                              requestId, request.getPeer()), e);
        }
    }

}

