package eu.smartsocietyproject.pf.orchestration;

import eu.smartsocietyproject.pf.CollectiveBase;
import eu.smartsocietyproject.pf.CollectiveBasedTask;
import eu.smartsocietyproject.pf.CollectiveWithPlan;
import eu.smartsocietyproject.pf.TaskRequest;
import eu.smartsocietyproject.pf.cbthandlers.*;

import java.util.List;

public interface OrchestratorManagerProxy extends CompositionHandler, NegotiationHandler, ContinuousOrchestrationHandler {
    boolean withdraw(CollectiveBasedTask cbt) throws CBTLifecycleException;
}
