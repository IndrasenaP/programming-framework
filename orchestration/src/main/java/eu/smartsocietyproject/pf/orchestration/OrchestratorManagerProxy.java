package eu.smartsocietyproject.pf.orchestration;

import eu.smartsocietyproject.pf.Collective;
import eu.smartsocietyproject.pf.CollectiveBasedTask;
import eu.smartsocietyproject.pf.TaskRequest;

import java.util.List;

public interface OrchestratorManagerProxy {
    List<CollectiveWithPlan> compose(Collective provisioned, TaskRequest t) throws CompositionException;

    CollectiveWithPlan negotiate(List<CollectiveWithPlan> negotiables) throws NegotiationException;

    CollectiveWithPlan continuousOrchestration(TaskRequest t) throws ContinuousOrchestrationException;
    boolean withdraw(CollectiveBasedTask cbt) throws ContinuousOrchestrationException;
}
