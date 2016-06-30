package eu.smartsocietyproject.pf.orchestration;

import eu.smartsocietyproject.pf.CollectiveBase;
import eu.smartsocietyproject.pf.CollectiveBasedTask;
import eu.smartsocietyproject.pf.CollectiveWithPlan;
import eu.smartsocietyproject.pf.TaskRequest;
import eu.smartsocietyproject.pf.cbthandlers.*;

import java.util.List;

public interface OrchestratorManagerProxy extends CompositionHandler, NegotiationHandler {
    //List<CollectiveWithPlan> compose(CollectiveBase provisioned, TaskRequest t) throws CBTLifecycleException;

    //CollectiveWithPlan negotiate(List<CollectiveWithPlan> negotiables) throws CBTLifecycleException;

    CollectiveWithPlan continuousOrchestration(TaskRequest t) throws CBTLifecycleException;
    boolean withdraw(CollectiveBasedTask cbt) throws CBTLifecycleException;
}
