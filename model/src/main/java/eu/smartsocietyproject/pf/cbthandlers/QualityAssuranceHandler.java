package eu.smartsocietyproject.pf.cbthandlers;

import akka.actor.Actor;
import eu.smartsocietyproject.pf.ApplicationContext;
import eu.smartsocietyproject.pf.TaskRequest;
import eu.smartsocietyproject.pf.TaskResult;

public interface QualityAssuranceHandler extends Actor {
    void qualityAssurance(ApplicationContext context, TaskResult taskResult);
}
