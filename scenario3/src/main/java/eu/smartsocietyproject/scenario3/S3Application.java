package eu.smartsocietyproject.scenario3;

import com.google.common.collect.ImmutableSet;
import com.typesafe.config.Config;
import eu.smartsocietyproject.pf.*;

import java.util.Optional;
import java.util.Set;

public class S3Application extends eu.smartsocietyproject.pf.Application {
    public final static String RIDESHARING = "ridesharing";
    public final static String DEVELOPERS_CBT_TYPE = "developers";
    public final static String TESTERS_CBT_TYPE = "testers";
    private ApplicationContext context = null;

    public S3Application() {
    }

    @Override
    public String getApplicationId() {
        return "Scenario4";
    }

    @Override
    public void init(ApplicationContext context, Config config) {
        this.context = context;

        context.registerBuilderForCBTType(RIDESHARING,
                                          CBTBuilder.empty()
                                                    .withContinuousOrchestrationHandler(new ImplicitAgreementByRatio(0.5)));
        context.registerBuilderForCBTType(TESTERS_CBT_TYPE,
                                          CBTBuilder.empty().asOnDemand()
                                                    .withProvisioningHandler(new IdentityProvisioningHandler())
                                                    .withNegotiationHandler(new ImplicitAgreementByRatio()));
    }

    @Override
    public Set<CollectiveKind> listCollectiveKinds() {
        return ImmutableSet.of(
            CollectiveKind.builder(DEVELOPERS_KIND).build()
        );
    }

    @Override
    public TaskRequest createTaskRequest(TaskDefinition definition) {
        return new S3TaskRequest(definition, "codingTask", Optional.empty());
    }

    @Override
    public TaskRunner createTaskRunner(TaskRequest request) {
        return new S3TaskRunner(context, (S3TaskRequest)request);
    }

}
