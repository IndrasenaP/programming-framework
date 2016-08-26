package eu.smartsocietyproject.pf.cbthandlers;

import eu.smartsocietyproject.pf.ApplicationContext;
import eu.smartsocietyproject.pf.CollectiveWithPlan;

import java.util.List;

public interface NegotiationHandler{
    CollectiveWithPlan negotiate(ApplicationContext context, List<CollectiveWithPlan> negotiables)
        throws CBTLifecycleException;
}



