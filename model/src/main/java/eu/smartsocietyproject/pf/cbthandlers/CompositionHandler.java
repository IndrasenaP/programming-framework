package eu.smartsocietyproject.pf.cbthandlers;

import akka.actor.AbstractActor;
import akka.actor.Actor;
import eu.smartsocietyproject.pf.*;

import java.util.List;

public interface CompositionHandler extends Actor {
    void compose(ApplicationContext context, ApplicationBasedCollective provisioned, TaskRequest t) throws CBTLifecycleException;
}

