package eu.smartsocietyproject.pf;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import com.google.common.collect.ImmutableList;
import eu.smartsocietyproject.pf.cbthandlers.CBTLifecycleException;
import eu.smartsocietyproject.pf.enummerations.LaborMode;
import eu.smartsocietyproject.pf.enummerations.State;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

public class CBTRunner extends AbstractActor {

    private final ApplicationContext context;
    private final TaskRequest request;
    private final TaskFlowDefinition definition;
    private ActorRef parent;
    private Set<LaborMode> laborMode;
    private boolean cancel;
    private ApplicationBasedCollective provisioningABC;
    private List<CollectiveWithPlan> negotiables;
    private CollectiveWithPlan negotiationABC;
    private TaskResult executionResult;
    private CollectiveWithPlan continuousOrchestrationABC;
    private State state;
    private Collective inputCollective;

    private boolean isOnDemand() {
        return laborMode.contains(LaborMode.ON_DEMAND);
    }

    private boolean isOpenCall() {
        return laborMode.contains(LaborMode.OPEN_CALL);
    }

    private CBTRunner(
            ApplicationContext context,
            TaskRequest request,
            TaskFlowDefinition definition) {
        this.context = context;
        this.request = request;
        this.definition = definition;
        this.state = State.INITIAL;
        if (definition.getCollectiveforProvisioning().isPresent())
            this.inputCollective = definition.getCollectiveforProvisioning().get();
    }

    @Override
    public void preStart() throws Exception {
        this.parent = getContext().getParent();
    }

    public void run() throws CBTLifecycleException {


            if (isOnDemand()) {
                parent.tell(State.WAITING_FOR_PROVISIONING, parent);
            } else {
                parent.tell(State.WAITING_FOR_CONTINUOUS_ORCHESTRATION, parent);
            }

            parent.tell(State.PROVISIONING, parent);

            this.provisioningABC = definition.getProvisioningHandlers().get(0)
                    .provision(context, request, definition.getCollectiveforProvisioning());

            if (isOpenCall())
                parent.tell(State.WAITING_FOR_COMPOSITION, parent);
            else
                parent.tell(State.WAITING_FOR_NEGOTIATION, parent);


            parent.tell(State.COMPOSITION, parent);

            this.negotiables = definition.getCompositionHandlers().get(0)
                    .compose(context, provisioningABC, request);

            parent.tell(State.WAITING_FOR_NEGOTIATION, parent);

            parent.tell(State.NEGOTIATION, parent);

            List<CollectiveWithPlan> negotiables =
                    this.isOpenCall()
                            ? this.negotiables
                            : ImmutableList.of(CollectiveWithPlan.of(this.provisioningABC, Plan.empty));

            this.negotiationABC = definition.getNegotiationHandlers().get(0)
                    .negotiate(context, negotiables);

            parent.tell(State.WAITING_FOR_EXECUTION, parent);

            parent.tell(State.EXECUTION, parent);

            this.executionResult = definition.getExecutionHandlers().get(0).execute(context, this.negotiationABC);


    }

    @Override
    public Receive createReceive() {
        return null;
    }

    public boolean isCancelled() {
        return this.cancel;
    }

    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }
}
