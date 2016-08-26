package eu.smartsocietyproject.pf.orchestration;

import eu.smartsocietyproject.pf.CollectiveBasedTask;
import eu.smartsocietyproject.pf.cbthandlers.*;

public interface OrchestratorManagerProxy extends CompositionHandler, NegotiationHandler, ContinuousOrchestrationHandler {
    boolean withdraw(CollectiveBasedTask cbt) throws CBTLifecycleException;
}
