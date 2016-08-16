package eu.smartsocietyproject.pf;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Map;

public abstract class Application {

    /* Defines a map of types (as a String) to TaskFlowDefinitions */
    public abstract Map<String, TaskFlowDefinition> defineTaskFlowsByType();

    /**
     * Extract Domain dependent Task Descriptor from the task definition
     *
     * @param definition generic smart society task definition
     * @return domain specific task descriptor
     */
    public abstract TaskRequest createTaskRequest(TaskDefinition definition);

    /** Returns the runner implementing the application task logic. It will be run in its own thread.
     * @param cbt the CollectiveBased Task associated to the task
     * @return the task runner implementing application logic
     */
    public abstract TaskRunner getTaskRunner(CollectiveBasedTask cbt);
}
