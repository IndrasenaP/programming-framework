package eu.smartsocietyproject.pf;

import akka.actor.ActorRef;
import akka.actor.Props;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import eu.smartsocietyproject.pf.adaptationPolicy.*;
import eu.smartsocietyproject.pf.cbthandlers.*;
import eu.smartsocietyproject.pf.enummerations.LaborMode;
import eu.smartsocietyproject.pf.CollectiveBasedTask;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * The definition of the workflow of a collective based task, with the instance of handlers to be used in the various
 * phases. The class is immutable and designed with a fluent interface.
 *
 * A flow is valid whether the information contained are consistent for being used with the {@link CollectiveBasedTask}
 * logic. Partial flows might be defined. Before actually creating
 * the CBT (through the {@link CBTBuilder#build()} method), the user should take care that all the handlers needed are
 * provided (according to the labour mode).
 *
 * The use of the static factory methods:
 * <ul>
 * <li>{@link #onDemandWithOpenCall(List, List, List, List, List)};</li>
 * <li>{@link #onDemandWithoutOpenCall(List, List, List, List)}</li>
 * <li>{@link #usingContinuousOrchestration(List, List, List)}</li>
 * </ul>
 *
 * makes it easier to create valid task flow definitions.
 *
 * This class is encapsulated by the {@link CBTBuilder} and used by {@link CollectiveBasedTask} for it's instantiation
 * and execution
 */
public class TaskFlowDefinition {

    private final ImmutableSet<LaborMode> laborMode;
    private final ImmutableList<Props> provisioningHandlers;
    private final ImmutableList<Props> compositionHandlers;
    private final ImmutableList<Props> negotiationHandlers;
    private final ImmutableList<Props> executionHandlers;
    private final ImmutableList<Props> qualityAssuranceHandlers;
    private final ImmutableList<Props> continuousOrchestrationHandlers;
    private final Collective collectiveForProvisioning;
    private final ProvisioningAdaptationPolicy provisioningAdaptationPolicy;
    private final CompositionAdaptationPolicy compositionAdaptationPolicy;
    private final NegotiationAdaptationPolicy negotiationAdaptationPolicy;
    private final ExecutionAdaptationPolicy executionAdaptationPolicy;
    private final QualityAssuranceAdaptionPolicy qualityAssuranceAdaptionPolicy;

    public TaskFlowDefinition(Set<LaborMode> laborMode, List<Props> provisioningHandlers, List<Props> compositionHandlers, List<Props> negotiationHandlers, List<Props> executionHandlers,
                              List<Props> qualityAssuranceHandlers, List<Props> continuousOrchestrationHandlers, Collective collectiveForProvisioning, ProvisioningAdaptationPolicy provisioningAdaptationPolicy,
                              CompositionAdaptationPolicy compositionAdaptationPolicy, NegotiationAdaptationPolicy negotiationAdaptationPolicy, ExecutionAdaptationPolicy executionAdaptationPolicy,
                              QualityAssuranceAdaptionPolicy qualityAssuranceAdaptionPolicy) {
        this.laborMode = ImmutableSet.copyOf(laborMode);
        this.provisioningHandlers = ImmutableList.copyOf(provisioningHandlers);
        this.compositionHandlers = ImmutableList.copyOf(compositionHandlers);
        this.negotiationHandlers = ImmutableList.copyOf(negotiationHandlers);
        this.executionHandlers = ImmutableList.copyOf(executionHandlers);
        this.qualityAssuranceHandlers = ImmutableList.copyOf(qualityAssuranceHandlers);
        this.continuousOrchestrationHandlers = ImmutableList.copyOf(continuousOrchestrationHandlers);
        this.collectiveForProvisioning = collectiveForProvisioning;
        this.provisioningAdaptationPolicy = provisioningAdaptationPolicy;
        this.compositionAdaptationPolicy = compositionAdaptationPolicy;
        this.negotiationAdaptationPolicy = negotiationAdaptationPolicy;
        this.executionAdaptationPolicy = executionAdaptationPolicy;
        this.qualityAssuranceAdaptionPolicy = qualityAssuranceAdaptionPolicy;
    }

    private TaskFlowDefinition(
            Set<LaborMode> laborMode,
            List<Props> provisioningHandlers,
            List<Props> compositionHandlers,
            List<Props> negotiationHandlers,
            List<Props> executionHandlers,
            List<Props> qualityAssuranceHandlers,
            List<Props> continuousOrchestrationHandlers,
            Collective collectiveForProvisioning) {
        this.laborMode = ImmutableSet.copyOf(laborMode);
        this.provisioningHandlers = ImmutableList.copyOf(provisioningHandlers);
        this.compositionHandlers = ImmutableList.copyOf(compositionHandlers);
        this.negotiationHandlers = ImmutableList.copyOf(negotiationHandlers);
        this.executionHandlers = ImmutableList.copyOf(executionHandlers);
        this.qualityAssuranceHandlers = ImmutableList.copyOf(qualityAssuranceHandlers);
        this.continuousOrchestrationHandlers = ImmutableList.copyOf(continuousOrchestrationHandlers);
        this.collectiveForProvisioning = collectiveForProvisioning;
        this.provisioningAdaptationPolicy = AdaptationPolicies.abort();
        this.compositionAdaptationPolicy = AdaptationPolicies.abort();
        this.negotiationAdaptationPolicy = AdaptationPolicies.abort();
        this.executionAdaptationPolicy = AdaptationPolicies.abort();
        this.qualityAssuranceAdaptionPolicy = AdaptationPolicies.abort();
    }

    private TaskFlowDefinition() {
        laborMode = ImmutableSet.copyOf(EnumSet.noneOf(LaborMode.class));
        provisioningHandlers = null;
        compositionHandlers = null;
        negotiationHandlers = null;
        continuousOrchestrationHandlers = null;
        executionHandlers = null;
        qualityAssuranceHandlers = null;
        collectiveForProvisioning = null;
        this.provisioningAdaptationPolicy = AdaptationPolicies.abort();
        this.compositionAdaptationPolicy = AdaptationPolicies.abort();
        this.negotiationAdaptationPolicy = AdaptationPolicies.abort();
        this.executionAdaptationPolicy = AdaptationPolicies.abort();
        this.qualityAssuranceAdaptionPolicy = AdaptationPolicies.abort();
    }

    /** Get the handler that will be used for provisioning in the CBT
     * @return handler for provisioning
     *
     * @exception IllegalStateException if provisioning handler has not been previously set
     */
    public ImmutableList<Props> getProvisioningHandlers() {
        Preconditions.checkState(provisioningHandlers!=null, "Provisioning Handlers not defined");
        return provisioningHandlers;
    }

    /* Get the handler used for composition.
     * @return handler for composition
     *
     * @exception IllegalStateException if composition handler has not been previously set
     * */
    public ImmutableList<Props> getCompositionHandlers() {
        Preconditions.checkState(compositionHandlers != null, "Composition Handlers not defined");
        return compositionHandlers;
    }

    /* Get the handler used for negotiation.
     * @return handler for negotiation
     *
     * @exception IllegalStateException if negotiation handler has not been previously set
     * */
    public ImmutableList<Props> getNegotiationHandlers() {
        Preconditions.checkState(negotiationHandlers != null, "Negotiation Handlers not defined");
        return negotiationHandlers;
    }

    /* Get the handler used for execution.
     * @return handler for execution
     *
     * @exception IllegalStateException if execution handler has not been previously set
     * */
    public ImmutableList<Props> getExecutionHandlers() {
        Preconditions.checkState(executionHandlers != null, "Execution Handlers not defined");
        return executionHandlers;
    }

    public ImmutableList<Props> getQualityAssuranceHandlers(){
        Preconditions.checkState(provisioningHandlers != null, "QualityAssurance Handlers not defined");
        return qualityAssuranceHandlers;
    }

    /* Get the handler used for negotiation.
     * @return handler for negotiation
     *
     * @exception IllegalStateException if negotiation handler has not been previously set
     * */
    public ImmutableList<Props> getContinuousOrchestrationHandlers() {
        Preconditions.checkState(continuousOrchestrationHandlers != null,
                "Continuous Orchestration Handler not defined");
        return continuousOrchestrationHandlers;
    }

    /* Get the labor mode that will be used in the CBT.
     * @return an Set of LaborMode
     *
     * @exception IllegalStateException if negotiation handler has not been previously set
     * */
    public ImmutableSet<LaborMode> getLaborMode() {
        return laborMode;
    }

    /* Get the optional Collective that might be used for the provisioning
     * @return an Set of LaborMode
     *
     * @exception IllegalStateException if negotiation handler has not been previously set
     * */
    public Optional<Collective> getCollectiveForProvisioning() {
        return Optional.ofNullable(collectiveForProvisioning);
    }

    /** Create the flow definition for a task that requires continuous orchestration
     *
     * @param continuousOrchestrationHandlers
     * @return a TaskFlowDefinition with labor mode: {@link LaborMode#OPEN_CALL}
     */
    public static TaskFlowDefinition usingContinuousOrchestration(
            List<Props> continuousOrchestrationHandlers,
            List<Props> executionHandlers,
            List<Props> qualityAssuranceHandlers) {
        Preconditions.checkNotNull(continuousOrchestrationHandlers);
        Preconditions.checkNotNull(executionHandlers);
        Preconditions.checkNotNull(qualityAssuranceHandlers);
        EnumSet<LaborMode> laborMode = EnumSet.of(LaborMode.OPEN_CALL);
        return new TaskFlowDefinition(laborMode, null, null, null, executionHandlers,
                qualityAssuranceHandlers, continuousOrchestrationHandlers,null);
    }

    /** Create the flow definition for an on demand task with explicit composition
     *
     * @param provisioningHandlers the handler used for provisioning
     * @param compositionHandlers the handler used for composition
     * @param negotiationHandlers the negotiation used for negotiation
     * @return a TaskFlowDefinition with labor mode:  {@link LaborMode#OPEN_CALL},  {@link LaborMode#ON_DEMAND}
     */
    public static TaskFlowDefinition onDemandWithOpenCall(
            List<Props> provisioningHandlers,
            List<Props> compositionHandlers,
            List<Props> negotiationHandlers,
            List<Props> executionHandlers,
            List<Props> qualityAssuranceHandlers
    ) {
        Preconditions.checkNotNull(provisioningHandlers);
        Preconditions.checkNotNull(compositionHandlers);
        Preconditions.checkNotNull(negotiationHandlers);
        Preconditions.checkNotNull(executionHandlers);
        Preconditions.checkNotNull(qualityAssuranceHandlers);

        EnumSet<LaborMode> laborMode =
                EnumSet.of(LaborMode.ON_DEMAND, LaborMode.OPEN_CALL);
        return new TaskFlowDefinition(laborMode, provisioningHandlers, compositionHandlers, negotiationHandlers,
                executionHandlers, qualityAssuranceHandlers, null,null);
    }

    /** Create the flow definition for an on demand task where no composition is allowed
     *
     * @param provisioningHandlers the handler used for provisioning
     * @param negotiationHandlers the negotiation used for negotiation
     * @return a TaskFlowDefinition with labor mode: {@link LaborMode#ON_DEMAND}
     */
    public static TaskFlowDefinition onDemandWithoutOpenCall(
            List<Props> provisioningHandlers,
            List<Props> negotiationHandlers,
            List<Props> executionHandlers,
            List<Props> qualityAssuranceHandlers
    ) {
        Preconditions.checkNotNull(provisioningHandlers);
        Preconditions.checkNotNull(negotiationHandlers);
        Preconditions.checkNotNull(executionHandlers);
        Preconditions.checkNotNull(qualityAssuranceHandlers);

        EnumSet<LaborMode> laborMode = EnumSet.of(LaborMode.ON_DEMAND);
        return new TaskFlowDefinition(laborMode, provisioningHandlers, null, negotiationHandlers, executionHandlers, qualityAssuranceHandlers, null,
                null);
    }

    /** Create an empty task flow definition. Note that such definition will not be valid for any CBT
     *
     * @return an empty TaskFlowDefinition */
    public static TaskFlowDefinition empty() {
        return new TaskFlowDefinition();
    }

    /** Creates a new instance with {@link LaborMode#OPEN_CALL} added to the labor mode
     *
     * @return an updated TaskFlowDefinition */
    public TaskFlowDefinition asOpenCall() {
        return withLaborMode(LaborMode.OPEN_CALL);
    }

    /** Creates a new instance with {@link LaborMode#ON_DEMAND} added to the labor mode
     *
     * @return an updated TaskFlowDefinition */
    public TaskFlowDefinition asOnDemand() {
        return withLaborMode(LaborMode.ON_DEMAND);
    }

    private TaskFlowDefinition withLaborMode(LaborMode lm) {
        Set<LaborMode> ocSet = EnumSet.of(lm);

        return
                new TaskFlowDefinition(
                        Sets.union(laborMode, ocSet),
                        provisioningHandlers,
                        compositionHandlers,
                        negotiationHandlers,
                        executionHandlers,
                        qualityAssuranceHandlers,
                        continuousOrchestrationHandlers,
                        collectiveForProvisioning);
    }

    /** Creates a new instance with a new provisioning handler
     *
     * @param handlers
     * @return an updated TaskFlowDefinition */
    public TaskFlowDefinition withProvisioningHandlers(List<Props> handlers) {
        Preconditions.checkNotNull(handlers);
        return
                new TaskFlowDefinition(
                        laborMode,
                        handlers,
                        compositionHandlers,
                        negotiationHandlers,
                        executionHandlers,
                        qualityAssuranceHandlers,
                        continuousOrchestrationHandlers,
                        collectiveForProvisioning);
    }

    /** Creates a new instance with a new composition handler
     *
     * @param handlers
     * @return an updated TaskFlowDefinition */
    public TaskFlowDefinition withCompositionHandlers(List<Props> handlers) {
        Preconditions.checkNotNull(handlers);
        return
                new TaskFlowDefinition(
                        laborMode,
                        provisioningHandlers,
                        handlers,
                        negotiationHandlers,
                        executionHandlers,
                        qualityAssuranceHandlers,
                        continuousOrchestrationHandlers,
                        collectiveForProvisioning);
    }

    /** Creates a new instance with a new negotiation handler
     *
     * @param handlers
     * @return an updated TaskFlowDefinition */
    public TaskFlowDefinition withNegotiationHandlers(List<Props> handlers) {
        Preconditions.checkNotNull(handlers);
        return
                new TaskFlowDefinition(
                        laborMode,
                        provisioningHandlers,
                        compositionHandlers,
                        handlers,
                        executionHandlers,
                        qualityAssuranceHandlers,
                        continuousOrchestrationHandlers,
                        collectiveForProvisioning);
    }

    /** Creates a new instance with a new continuous orchestration handler
     *
     * @param handlers
     * @return an updated TaskFlowDefinition */
    public TaskFlowDefinition withContinuousOrchestrationHandlers(List<Props> handlers) {
        Preconditions.checkNotNull(handlers);
        return
                new TaskFlowDefinition(
                        laborMode,
                        provisioningHandlers,
                        compositionHandlers,
                        negotiationHandlers,
                        executionHandlers,
                        qualityAssuranceHandlers,
                        handlers,
                        collectiveForProvisioning);
    }

    /** Creates a new instance with a new execution handler
     *
     * @param handlers
     * @return an updated TaskFlowDefinition */
    public TaskFlowDefinition withExecutionHandlers(List<Props> handlers) {
        Preconditions.checkNotNull(handlers);
        return
                new TaskFlowDefinition(
                        laborMode,
                        provisioningHandlers,
                        compositionHandlers,
                        negotiationHandlers,
                        handlers,
                        qualityAssuranceHandlers,
                        continuousOrchestrationHandlers,
                        collectiveForProvisioning);
    }

    /** Creates a new instance with a new quality assurance handler
     *
     * @param handlers
     * @return an updated TaskFlowDefinition */
    public TaskFlowDefinition withQualityAssuranceHandlers(List<Props> handlers) {
        Preconditions.checkNotNull(handlers);
        return
                new TaskFlowDefinition(
                        laborMode,
                        provisioningHandlers,
                        compositionHandlers,
                        negotiationHandlers,
                        executionHandlers,
                        handlers,
                        continuousOrchestrationHandlers,
                        collectiveForProvisioning);
    }

    /** Creates a new instance with the provided collective for Provisioning
     *
     * @param collective the collective to be passed for provisioning
     * @return an updated TaskFlowDefinition */
    public TaskFlowDefinition withCollectiveForProvisioning(Collective collective) {
        Preconditions.checkNotNull(collective);
        return
                new TaskFlowDefinition(
                        laborMode,
                        provisioningHandlers,
                        compositionHandlers,
                        negotiationHandlers,
                        executionHandlers,
                        qualityAssuranceHandlers,
                        continuousOrchestrationHandlers,
                        collective);
    }

    public ProvisioningAdaptationPolicy getProvisioningAdaptationPolicy() {
        Preconditions.checkState(provisioningAdaptationPolicy != null, "Provision Adaptation Policy not defined");
        return provisioningAdaptationPolicy;
    }

    public CompositionAdaptationPolicy getCompositionAdaptationPolicy() {
        Preconditions.checkState(compositionAdaptationPolicy != null, "Composition Adaptation Policy not defined");
        return compositionAdaptationPolicy;
    }

    public NegotiationAdaptationPolicy getNegotiationAdaptationPolicy() {
        Preconditions.checkState(negotiationAdaptationPolicy != null, "Negotiation Adaptation Policy not defined");
        return negotiationAdaptationPolicy;
    }

    public ExecutionAdaptationPolicy getExecutionAdaptationPolicy() {
        Preconditions.checkState(executionAdaptationPolicy != null, "Execution Adaptation Policy not defined");
        return executionAdaptationPolicy;
    }

    public QualityAssuranceAdaptionPolicy getQualityAssuranceAdaptionPolicy() {
        Preconditions.checkState(qualityAssuranceAdaptionPolicy != null, "Quality Assurance Adaptation Policy not defined");
        return qualityAssuranceAdaptionPolicy;
    }

    /** Checks whether an instance is valid for being used in the construction of a
     * {@link CollectiveBasedTask}
     *
     * @return true if the instance is valid, false otherwise */
    public boolean isValid() {
        if ( laborMode == null ) return false;

        if ( laborMode.contains(LaborMode.ON_DEMAND) ) {
            if ( provisioningHandlers == null || negotiationHandlers == null)
                return false;
        } else {
            return continuousOrchestrationHandlers != null;
        }

        if ( laborMode.contains(LaborMode.OPEN_CALL) ) {
            return compositionHandlers != null;
        }

        return true;
    }
}
