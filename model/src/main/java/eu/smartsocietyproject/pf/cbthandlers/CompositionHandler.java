package eu.smartsocietyproject.pf.cbthandlers;

import eu.smartsocietyproject.pf.*;

import java.util.List;

public interface CompositionHandler{
    List<CollectiveWithPlan> compose(ApplicationContext context, ApplicationBasedCollective provisioned, TaskRequest t) throws CBTLifecycleException;
}

