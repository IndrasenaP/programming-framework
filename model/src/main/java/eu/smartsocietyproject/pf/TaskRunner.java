package eu.smartsocietyproject.pf;


import com.fasterxml.jackson.databind.JsonNode;
import eu.smartsocietyproject.TaskResponse;

import java.util.concurrent.Callable;

public interface TaskRunner extends Callable<TaskResponse> {
    JsonNode getStateDescription();
}
