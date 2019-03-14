package eu.smartsocietyproject.pf;


import akka.actor.Actor;
import com.fasterxml.jackson.databind.JsonNode;

public interface TaskRunner extends Actor {
    JsonNode getStateDescription();
}
