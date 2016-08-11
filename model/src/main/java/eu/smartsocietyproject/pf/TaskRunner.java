package eu.smartsocietyproject.pf;


import com.fasterxml.jackson.databind.JsonNode;

public interface TaskRunner extends Runnable {
    JsonNode getStateDescription();
}
