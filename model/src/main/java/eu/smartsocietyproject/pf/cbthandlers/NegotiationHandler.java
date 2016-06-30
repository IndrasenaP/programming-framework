package eu.smartsocietyproject.pf.cbthandlers;

import eu.smartsocietyproject.pf.CollectiveWithPlan;

import java.util.List;

public interface NegotiationHandler{
    CollectiveWithPlan negotiate(List<CollectiveWithPlan> negotiables) throws CBTLifecycleException;
}



