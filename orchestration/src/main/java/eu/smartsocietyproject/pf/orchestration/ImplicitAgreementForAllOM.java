package eu.smartsocietyproject.pf.orchestration;

import eu.smartsocietyproject.pf.Collective;
import eu.smartsocietyproject.pf.CollectiveBasedTask;
import eu.smartsocietyproject.pf.Plan;
import eu.smartsocietyproject.pf.TaskRequest;

import java.util.ArrayList;
import java.util.List;

public class ImplicitAgreementForAllOM implements OrchestratorManagerProxy {
    @Override
    public List<CollectiveWithPlan> compose(
        Collective provisioned, TaskRequest t) throws CompositionException {
//        throw new UnsupportedOperationException("TODO"); // -=TODO=-
//        OMInstance omi=new OMInstance();
//        List<CollectiveWithPlan> collectiveWithPlanList = omi.getPlans(provisioned,t);
        List<CollectiveWithPlan> collectiveWithPlanList = new ArrayList<CollectiveWithPlan>();
//        collectiveWithPlanList.add(CollectiveWithPlan.of(new Collective(), new Plan()));
        return  collectiveWithPlanList;
    }

    @Override
    public CollectiveWithPlan negotiate(List<CollectiveWithPlan> negotiables) throws NegotiationException {
//        throw new UnsupportedOperationException("TODO"); // -=TODO=-
        return negotiables.get(0);
    }

    @Override
    public CollectiveWithPlan continuousOrchestration(TaskRequest t) throws ContinuousOrchestrationException {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public boolean withdraw(CollectiveBasedTask cbt) throws ContinuousOrchestrationException {
        throw new UnsupportedOperationException("TODO");
    }
}
