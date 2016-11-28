package eu.smartsocietyproject.pf;

import com.typesafe.config.Config;

import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class Application {
    /** Application ID assigned by the SmartSociety during the application registration
     * @return
     */
    public abstract String getApplicationId();

    /** Use configuration for initializing application internals
     * @param context Application context
     * @param config typesafe configuration
     */
    public abstract void init(ApplicationContext context, Config config);

    /* Returns the collective kind that will be used by the application */
    public abstract Set<CollectiveKind> listCollectiveKinds();

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
    public abstract TaskRunner createTaskRunner(TaskRequest request);



}
