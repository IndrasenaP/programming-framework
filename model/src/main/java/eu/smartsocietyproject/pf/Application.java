package eu.smartsocietyproject.pf;

import com.typesafe.config.Config;

import java.util.Map;

public abstract class Application {

    /** Application ID assigned by the SmartSociety during the application registration
     * @return
     */
    public abstract String getApplicationId();

    /** Use configuration for initializing application internals
     * @param config typesafe configuration
     */
    public abstract void init(Config config);

    /* Defines a map of types (as a String) to TaskFlowDefinitions, called once after initialization */
    public abstract Map<String, TaskFlowDefinition> defineTaskFlowsByType();

    /**
     * Extract Domain dependent TaskRequest from the TaskDefinition
     *
     * @param definition generic smart society task definition
     * @return domain specific task descriptor
     */
    public abstract TaskRequest createTaskRequest(TaskDefinition definition);

    /** Returns the runner implementing the application task logic. It will be run in its own thread.
     * @param request the TaskRequest describing the task to be performed
     * @return the task runner implementing application logic
     */
    public abstract TaskRunner getTaskRunner(TaskRequest request);



}
