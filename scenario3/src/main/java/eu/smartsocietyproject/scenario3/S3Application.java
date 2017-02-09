package eu.smartsocietyproject.scenario3;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.typesafe.config.*;
import eu.smartsocietyproject.pf.*;
import eu.smartsocietyproject.pf.cbthandlers.ContinuousOrchestrationHandler;
import eu.smartsocietyproject.utils.RideSharingAPI;

import java.util.*;

public class S3Application extends eu.smartsocietyproject.pf.Application {
    public final static String EMPTYCOLLECTIVE = "empty";
    public final static String RIDESHARING_COLLECTIVE_KIND = "ridesharing";
    private ApplicationContext context = null;

    public S3Application() {
    }

    @Override
    public String getApplicationId() {
        return "Scenario4";
    }

    @Override
    public void init(ApplicationContext context, Config configIn) {
        Config config =
        configIn.withFallback(
            ConfigFactory.parseMap(ImmutableMap.of("orchestrator.polling", "60000"))
        );
        this.context = context;
        RideSharingAPI api = new RideSharingAPI(
            config.getString("orchestrator.url"),
            config.getString("orchestrator.secret"),
            config.getString("orchestrator.key")
        );
        ContinuousOrchestrationHandler handler =
            new RideSharingContinuousOrchestrationHandler(
                api, config.getLong("orchestrator.polling")
            );
        context.registerBuilderForCBTType(
            RIDESHARING_COLLECTIVE_KIND,
            CBTBuilder.empty().withContinuousOrchestrationHandler(handler));
    }

    @Override
    public Set<CollectiveKind> listCollectiveKinds() {
        return ImmutableSet.of(
            CollectiveKind.builder(RIDESHARING_COLLECTIVE_KIND).build()
        );
    }

    @Override
    public TaskRequest createTaskRequest(TaskDefinition definition) throws ApplicationException {
        return new S3TaskRequest(definition, "rideShare");
    }

    @Override
    public TaskRunner createTaskRunner(TaskRequest request) {
        return new S3TaskRunner(context, (S3TaskRequest)request);
    }

}
