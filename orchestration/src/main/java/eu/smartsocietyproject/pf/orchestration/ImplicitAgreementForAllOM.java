package eu.smartsocietyproject.pf.orchestration;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.PoisonPill;
import akka.actor.Props;
import com.google.common.collect.ImmutableList;
import eu.smartsocietyproject.pf.*;
import eu.smartsocietyproject.pf.cbthandlers.CBTLifecycleException;
import eu.smartsocietyproject.pf.cbthandlers.CBTLifecycleException;
import eu.smartsocietyproject.pf.cbthandlers.CBTLifecycleException;
import eu.smartsocietyproject.pf.enummerations.State;

import java.util.ArrayList;
import java.util.List;

public class ImplicitAgreementForAllOM extends AbstractActor implements OrchestratorManagerProxy {

    private ActorRef parent;
    private ApplicationContext context;
    private TaskRequest taskRequest;

    static public Props props(ApplicationContext context,
                              TaskRequest request) {
        return Props.create(ImplicitAgreementForAllOM.class, () -> new ImplicitAgreementForAllOM(context, request));
    }

    private ImplicitAgreementForAllOM(ApplicationContext context, TaskRequest taskRequest){
        this.context = context;
        this.taskRequest = taskRequest;
    }

    @Override
    public void preStart() throws Exception {
        this.parent = getContext().getParent();
    }

    @Override
    public void compose(ApplicationContext context,
        ApplicationBasedCollective provisioned, TaskRequest t) throws CBTLifecycleException {
        ImmutableList<CollectiveWithPlan> collectiveWithPlanList = ImmutableList.of(CollectiveWithPlan.of(provisioned, new Plan()));
        parent.tell(collectiveWithPlanList, getSelf());
    }

    private void compose(ApplicationBasedCollective provisioned) throws CBTLifecycleException {
        compose(context, provisioned, taskRequest);
    }

    @Override
    public void negotiate(ApplicationContext context, ImmutableList<CollectiveWithPlan> negotiables) throws CBTLifecycleException {
        parent.tell(negotiables.get(0), getSelf());
    }

    private void negotiate(ImmutableList<CollectiveWithPlan> negotiables) throws CBTLifecycleException {
        negotiate(context, negotiables);
    }

    @Override
    public CollectiveWithPlan continuousOrchestration(ApplicationContext context, TaskRequest t) throws CBTLifecycleException {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public boolean withdraw(CollectiveBasedTask cbt) throws CBTLifecycleException {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    @SuppressWarnings("unchecked")
    public Receive createReceive() {

        return receiveBuilder()
                .match(ApplicationBasedCollective.class,
                        abc -> {
                            compose(abc);
                            getSender().tell(PoisonPill.getInstance(), getSelf());
                        })
                .match(ImmutableList.class,
                        collectivesWithPlan -> {
                            negotiate((ImmutableList<CollectiveWithPlan>) collectivesWithPlan);
                        })
                .build();
    }
}
