package eu.smartsocietyproject.pf;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.PoisonPill;
import akka.actor.Props;
import com.google.common.collect.ImmutableList;
import eu.smartsocietyproject.pf.cbthandlers.CBTLifecycleException;
import eu.smartsocietyproject.pf.enummerations.LaborMode;
import eu.smartsocietyproject.pf.enummerations.State;

import java.util.Set;

public class CBTRunner extends AbstractActor {

    private final ApplicationContext context;
    private final TaskRequest request;
    private final TaskFlowDefinition definition;
    private ActorRef parent;
    private Set<LaborMode> laborMode;
    private boolean cancel;
    private ApplicationBasedCollective provisioningABC;
    private ImmutableList<CollectiveWithPlan> negotiables;
    private CollectiveWithPlan negotiationABC;
    private TaskResult executionResult;
    private CollectiveWithPlan continuousOrchestrationABC;
    private State state;
    private Collective inputCollective;

    static public Props props(ApplicationContext context,
                        TaskRequest request,
                        TaskFlowDefinition definition) {
        return Props.create(CBTRunner.class, () -> new CBTRunner(context, request, definition));
    }

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
        if (definition.getCollectiveForProvisioning().isPresent())
            this.inputCollective = definition.getCollectiveForProvisioning().get();

        if (isOnDemand()) {
            parent.tell(State.WAITING_FOR_PROVISIONING, parent);
        } else {
            parent.tell(State.WAITING_FOR_CONTINUOUS_ORCHESTRATION, parent);
        }
    }

    @Override
    public void preStart() throws Exception {
        this.parent = getContext().getParent();
    }


    private void provision(){

        parent.tell(State.PROVISIONING, parent);

        definition.getProvisioningHandler()
                .tell(definition.getCollectiveForProvisioning(), getSelf());

    }

    private void composition(ApplicationBasedCollective abc){

        parent.tell(State.WAITING_FOR_COMPOSITION, parent);

        this.provisioningABC = abc;


        definition.getCompositionHandler()
                .tell(provisioningABC, getSelf());

        parent.tell(State.COMPOSITION, parent);

    }
    private void negotiation(ImmutableList<CollectiveWithPlan> collectivesWithPlan){
        parent.tell(State.WAITING_FOR_NEGOTIATION, parent);

        this.negotiables =
                this.isOpenCall()
                        ? collectivesWithPlan
                        : ImmutableList.of(CollectiveWithPlan.of(this.provisioningABC, Plan.empty));


        definition.getNegotiationHandler()
                .tell(negotiables, getSelf());


        parent.tell(State.NEGOTIATION, parent);


    }

    private void execution(CollectiveWithPlan collectiveWithPlan){
        parent.tell(State.WAITING_FOR_EXECUTION, parent);

        definition.getExecutionHandler().tell(collectiveWithPlan, getSelf());

        parent.tell(State.EXECUTION, parent);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Receive createReceive() {

        return receiveBuilder()
                .match(State.class,
                            start -> {
                                if(start == State.START)
                                    provision();
                            }
                        )
                .match(ApplicationBasedCollective.class,
                        abc -> {
                            composition(abc);
                            getSender().tell(PoisonPill.getInstance(), getSelf());
                        })
                .match(ImmutableList.class,
                        collectivesWithPlan -> {
                            negotiation((ImmutableList<CollectiveWithPlan>) collectivesWithPlan);
                            getSender().tell(PoisonPill.getInstance(), getSelf());
                        })
                .match(CollectiveWithPlan.class,
                        collectiveWithPlan -> {
                            execution(collectiveWithPlan);
                            getSender().tell(PoisonPill.getInstance(), getSelf());
                        })
                .match(TaskResult.class,
                        taskResult -> parent.tell(taskResult, getSelf()))
                .build();
    }

    public boolean isCancelled() {
        return this.cancel;
    }

    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }
}
