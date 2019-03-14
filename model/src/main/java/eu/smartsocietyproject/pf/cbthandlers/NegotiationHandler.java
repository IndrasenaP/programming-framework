package eu.smartsocietyproject.pf.cbthandlers;

import akka.actor.AbstractActor;
import akka.actor.Actor;
import com.google.common.collect.ImmutableList;
import eu.smartsocietyproject.pf.ApplicationContext;
import eu.smartsocietyproject.pf.CollectiveWithPlan;

import java.util.List;

public interface NegotiationHandler extends Actor {
    void negotiate(ApplicationContext context, ImmutableList<CollectiveWithPlan> negotiables)
        throws CBTLifecycleException;
}



