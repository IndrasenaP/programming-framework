package eu.smartsocietyproject.pf;

import at.ac.tuwien.dsg.smartcom.exception.CommunicationException;
import at.ac.tuwien.dsg.smartcom.model.Identifier;
import at.ac.tuwien.dsg.smartcom.model.Message;
import eu.smartsocietyproject.pf.cbthandlers.CBTLifecycleException;
import eu.smartsocietyproject.pf.cbthandlers.ExecutionHandler;
import eu.smartsocietyproject.scenario3.S3Plan;

import java.util.Optional;

public class SmartComPlanCommunicationExecutionHandler implements ExecutionHandler {
    private Optional<TaskResult> result = Optional.empty();

    @Override
    public TaskResult execute(ApplicationContext context, CollectiveWithPlan agreed) throws CBTLifecycleException {
        S3Plan plan = (S3Plan) agreed.getPlan();
        Message m =
            new Message.MessageBuilder()
                .setContent(plan.getPlanId())
                .setConversationId(plan.getRequest().getConversation())
                .setReceiverId(Identifier.peer(plan.getRequest().getPeer()))
            .setType("Scenario3")
            .setSubtype("PlanReady")
            .setContent(plan.getPlanId())
            .create();
        try {
            context.getSmartCom().send(m);
        } catch (CommunicationException e) {
            throw new CBTLifecycleException(
                String.format("Unable to communicate plan %s to the peer %s",
                              plan.getPlanId(), plan.getRequest().getPeer()), e);
        }
        result=Optional.of(new TaskResult() {});
        return getResultIfQoRGoodEnough();
    }

    @Override
    public double resultQoR() {
        return result.isPresent()?1.0:0.0;
    }

    @Override
    public TaskResult getResultIfQoRGoodEnough() {
        return result.orElse(null);
    }
}
