package eu.smartsocietyproject.pf.orchestration;

import eu.smartsocietyproject.pf.Collective;
import eu.smartsocietyproject.pf.CollectiveBasedTask;
import eu.smartsocietyproject.pf.TaskRequest;

import java.util.List;

public class ImplicitAgreementForAllOM implements OrchestratorManager {
    @Override
    public List<CollectiveWithPlan> compose(
        Collective provisioned, TaskRequest t) throws CompositionException {
        throw new UnsupportedOperationException("TODO"); // -=TODO=-
    }

    @Override
    public CollectiveWithPlan negotiate(List<CollectiveWithPlan> negotiables) throws NegotiationException {
        throw new UnsupportedOperationException("TODO"); // -=TODO=-
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
