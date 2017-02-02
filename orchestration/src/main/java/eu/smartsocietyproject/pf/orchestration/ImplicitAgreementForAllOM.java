package eu.smartsocietyproject.pf.orchestration;

import eu.smartsocietyproject.pf.*;
import eu.smartsocietyproject.pf.cbthandlers.CBTLifecycleException;
import eu.smartsocietyproject.pf.cbthandlers.CBTLifecycleException;
import eu.smartsocietyproject.pf.cbthandlers.CBTLifecycleException;

import java.util.ArrayList;
import java.util.List;

public class ImplicitAgreementForAllOM implements OrchestratorManagerProxy {
    @Override
    public List<CollectiveWithPlan> compose(ApplicationContext context,
        ApplicationBasedCollective provisioned, TaskRequest t) throws CBTLifecycleException {
        List<CollectiveWithPlan> collectiveWithPlanList = new ArrayList<>();
        collectiveWithPlanList.add(CollectiveWithPlan.of(provisioned, new Plan()));
        return  collectiveWithPlanList;
    }

    @Override
    public CollectiveWithPlan negotiate(ApplicationContext context, List<CollectiveWithPlan> negotiables) throws CBTLifecycleException {
        return negotiables.get(0);
    }

    @Override
    public CollectiveWithPlan continuousOrchestration(ApplicationContext context, TaskRequest t) throws CBTLifecycleException {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public boolean withdraw(CollectiveBasedTask cbt) throws CBTLifecycleException {
        throw new UnsupportedOperationException("TODO");
    }
}
