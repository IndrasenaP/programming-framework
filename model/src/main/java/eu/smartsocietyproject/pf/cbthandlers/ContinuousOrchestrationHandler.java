package eu.smartsocietyproject.pf.cbthandlers;

import eu.smartsocietyproject.pf.ApplicationContext;
import eu.smartsocietyproject.pf.Collective;
import eu.smartsocietyproject.pf.CollectiveWithPlan;
import eu.smartsocietyproject.pf.TaskRequest;

import java.util.List;

public interface ContinuousOrchestrationHandler {
    CollectiveWithPlan continuousOrchestration(ApplicationContext context, TaskRequest t) throws CBTLifecycleException;
}

