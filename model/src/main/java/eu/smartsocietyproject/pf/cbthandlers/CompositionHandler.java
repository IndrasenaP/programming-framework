package eu.smartsocietyproject.pf.cbthandlers;

import eu.smartsocietyproject.pf.ApplicationBasedCollective;
import eu.smartsocietyproject.pf.Collective;
import eu.smartsocietyproject.pf.CollectiveWithPlan;
import eu.smartsocietyproject.pf.TaskRequest;
import java.util.List;

public interface CompositionHandler{
    List<CollectiveWithPlan> compose(ApplicationBasedCollective provisioned, TaskRequest t) throws CBTLifecycleException;
}

