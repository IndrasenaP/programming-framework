package eu.smartsocietyproject.pf;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import eu.smartsocietyproject.pf.cbthandlers.CompositionHandler;
import eu.smartsocietyproject.pf.cbthandlers.ContinuousOrchestrationHandler;
import eu.smartsocietyproject.pf.cbthandlers.NegotiationHandler;
import eu.smartsocietyproject.pf.cbthandlers.ProvisioningHandler;

import java.util.EnumSet;
import java.util.Set;
import java.util.function.Function;

public class TaskFlowDefinition {
    private final ImmutableSet<CollectiveBasedTask.LaborMode> laborMode;
    private final ProvisioningHandler provisioningHandler;
    private final CompositionHandler compositionHandler;
    private final NegotiationHandler negotiationHandler;
    private final ContinuousOrchestrationHandler continuousOrchestrationHandler;
    private final Function<TaskRequest, Object[]> negotiationArgumentSetter;

    private TaskFlowDefinition(
        Set<CollectiveBasedTask.LaborMode> laborMode,
        ProvisioningHandler provisioningHandler,
        CompositionHandler compositionHandler,
        NegotiationHandler negotiationHandler,
        ContinuousOrchestrationHandler continuousOrchestrationHandler,
        Function<TaskRequest, Object[]> negotiationArgumentSetter) {
        this.laborMode = ImmutableSet.copyOf(laborMode);
        this.provisioningHandler = provisioningHandler;
        this.compositionHandler = compositionHandler;
        this.negotiationHandler = negotiationHandler;
        this.continuousOrchestrationHandler = continuousOrchestrationHandler;
        this.negotiationArgumentSetter = negotiationArgumentSetter;
    }

    public ProvisioningHandler getProvisioningHandler() {
        if (provisioningHandler == null)
            throw new IllegalStateException("Provisioning Handler not defined");
        return provisioningHandler;
    }

    public CompositionHandler getCompositionHandler() {
        if (compositionHandler == null)
            throw new IllegalStateException("Composition Handler not defined");
        return compositionHandler;
    }

    public NegotiationHandler getNegotiationHandler() {
        if (negotiationHandler == null)
            throw new IllegalStateException("Negotiation Handler not defined");
        return negotiationHandler;
    }

    public ContinuousOrchestrationHandler getContinuousOrchestrationHandler() {
        if (continuousOrchestrationHandler == null)
            throw new IllegalStateException("Continuous Orchestration Handler not defined");
        return continuousOrchestrationHandler;
    }

    public ImmutableSet<CollectiveBasedTask.LaborMode> getLaborMode() {
        return laborMode;
    }

    public static TaskFlowDefinition withContinuousOrchestration(ContinuousOrchestrationHandler continuousOrchestrationHandler) {
        Preconditions.checkNotNull(continuousOrchestrationHandler);
        EnumSet<CollectiveBasedTask.LaborMode> laborMode = EnumSet.of(CollectiveBasedTask.LaborMode.OPEN_CALL);
        return new TaskFlowDefinition(laborMode, null, null, null, continuousOrchestrationHandler, null);
    }

    public static TaskFlowDefinition onDemandWithOpenCall(
        ProvisioningHandler provisioningHandler,
        CompositionHandler compositionHandler,
        NegotiationHandler negotiationHandler
    ) {
        Preconditions.checkNotNull(provisioningHandler);
        Preconditions.checkNotNull(compositionHandler);
        Preconditions.checkNotNull(negotiationHandler);

        EnumSet<CollectiveBasedTask.LaborMode> laborMode =
            EnumSet.of(CollectiveBasedTask.LaborMode.ON_DEMAND, CollectiveBasedTask.LaborMode.OPEN_CALL);
        return new TaskFlowDefinition(laborMode, provisioningHandler, compositionHandler, negotiationHandler, null, null);
    }

    public static TaskFlowDefinition onDemandWithoutOpenCall(
        ProvisioningHandler provisioningHandler,
        NegotiationHandler negotiationHandler
    ) {
        Preconditions.checkNotNull(provisioningHandler);
        Preconditions.checkNotNull(negotiationHandler);

        EnumSet<CollectiveBasedTask.LaborMode> laborMode = EnumSet.of(CollectiveBasedTask.LaborMode.ON_DEMAND);
        return new TaskFlowDefinition(laborMode, provisioningHandler, null, negotiationHandler, null, null);
    }

    public TaskFlowDefinition setNegotiationArgumentsWith(Function<TaskRequest, Object[]> argSetter) {
        return
            new TaskFlowDefinition(
                laborMode,
                provisioningHandler,
                compositionHandler,
                negotiationHandler,
                continuousOrchestrationHandler,
                argSetter);
    }

    public Object[] getNegotiationArguments(TaskRequest request) {
        return negotiationArgumentSetter == null
               ? new Object[0]
               : negotiationArgumentSetter.apply(request);
    }
}
