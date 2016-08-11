package eu.smartsocietyproject.pf;

import com.fasterxml.jackson.databind.JsonNode;

public abstract class Application {
    /**
     * Extract Domain dependent Task Request from the task definition
     *
     * @param definition generic smart society task definition
     * @return domain specific task request
     */
    public abstract TaskRequest extractTaskRequest(TaskDefinition definition);

    /** Returns the runner implementing the application task logic. It will be run in its own thread.
     * @param request the domain dependent task request
     * @param cbt the CollectiveBased Task associated to the task
     * @return the task runner implementing application logic
     */
    public abstract TaskRunner getTaskRunner(TaskRequest request, CollectiveBasedTask cbt);
}
