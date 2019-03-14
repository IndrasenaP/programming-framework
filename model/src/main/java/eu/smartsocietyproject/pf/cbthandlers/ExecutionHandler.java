package eu.smartsocietyproject.pf.cbthandlers;

import akka.actor.AbstractActor;
import akka.actor.Actor;
import eu.smartsocietyproject.pf.ApplicationContext;
import eu.smartsocietyproject.pf.CollectiveWithPlan;
import eu.smartsocietyproject.pf.TaskResult;
import eu.smartsocietyproject.pf.adaptationPolicy.ExecutionAdaptationPolicy;

import java.util.List;

public interface ExecutionHandler extends Actor {

    void execute(ApplicationContext context, CollectiveWithPlan agreed) throws CBTLifecycleException;

}
