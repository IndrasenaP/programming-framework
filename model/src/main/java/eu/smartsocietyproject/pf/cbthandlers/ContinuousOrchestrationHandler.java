package eu.smartsocietyproject.pf.cbthandlers;

import eu.smartsocietyproject.pf.Collective;
import eu.smartsocietyproject.pf.CollectiveWithPlan;
import eu.smartsocietyproject.pf.TaskRequest;

import java.util.List;

public interface ContinuousOrchestrationHandler {
    CollectiveWithPlan continuousOrchestration(TaskRequest t) throws CBTLifecycleException;
}

