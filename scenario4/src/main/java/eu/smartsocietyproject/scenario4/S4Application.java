package eu.smartsocietyproject.scenario4;

import com.google.common.collect.ImmutableSet;
import com.typesafe.config.Config;
import eu.smartsocietyproject.pf.*;

import java.util.Optional;
import java.util.Set;

public class S4Application extends eu.smartsocietyproject.pf.Application {
    public final static String DEVELOPERS_KIND = "developers";
    public final static String DEVELOPERS_CBT_TYPE = "developers";
    public final static String TESTERS_CBT_TYPE = "testers";
    private ApplicationContext context = null;

    public S4Application() {
    }

    @Override
    public String getApplicationId() {
        return "Scenario4";
    }

    @Override
    public void init(ApplicationContext context, Config config) {
        this.context = context;

        context.registerBuilderForCBTType(DEVELOPERS_CBT_TYPE,
                                          CBTBuilder.empty().asOnDemand()
                                                    .withNegotiationHandler(new ImplicitAgreementByRatio(0.5)));
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
        return new S4TaskRequest(definition, "codingTask", Optional.empty());
    }

    @Override
    public TaskRunner createTaskRunner(TaskRequest request) {
        return new S4TaskRunner(context, (S4TaskRequest)request);
    }

}
