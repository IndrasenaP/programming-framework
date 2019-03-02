package eu.smartsocietyproject.pf;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import eu.smartsocietyproject.pf.adaptationPolicy.AbortPolicy;
import eu.smartsocietyproject.pf.adaptationPolicy.AdaptationPolicies;
import eu.smartsocietyproject.pf.adaptationPolicy.CompositionAdaptationPolicy;
import eu.smartsocietyproject.pf.adaptationPolicy.ExecutionAdaptationPolicy;
import eu.smartsocietyproject.pf.adaptationPolicy.NegotiationAdaptationPolicy;
import eu.smartsocietyproject.pf.adaptationPolicy.ProvisioningAdaptationPolicy;
import eu.smartsocietyproject.pf.cbthandlers.*;
import eu.smartsocietyproject.pf.CollectiveBasedTask;
import eu.smartsocietyproject.pf.enummerations.LaborMode;
import eu.smartsocietyproject.pf.cbthandlers.CompositionHandler;
import eu.smartsocietyproject.pf.cbthandlers.ExecutionHandler;
import eu.smartsocietyproject.pf.cbthandlers.ContinuousOrchestrationHandler;
import eu.smartsocietyproject.pf.CBTBuilder;

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
 * <li>{@link #onDemandWithOpenCall(ImmutableList, ImmutableList, ImmutableList, ImmutableList)};</li>
 * <li>{@link #onDemandWithoutOpenCall(ImmutableList, ImmutableList, ImmutableList)}</li>
 * <li>{@link #usingContinuousOrchestration(ContinuousOrchestrationHandler, ImmutableList)}</li>
 * </ul>
 *
 * makes it easier to create valid task flow definitions.
 *
 * This class is encapsulated by the {@link CBTBuilder} and used by {@link CollectiveBasedTask} for it's instantiation
 * and execution
 */
public class TaskFlowDefinition {
    private final ImmutableSet<LaborMode> laborMode;
    
    private final ImmutableList<ProvisioningHandler> provisioningHandlers;
    private final ImmutableList<CompositionHandler> compositionHandlers;
    private final ImmutableList<NegotiationHandler> negotiationHandlers;
    private final ImmutableList<ExecutionHandler> executionHandlers;
    
    private final ProvisioningAdaptationPolicy provisioningAdaptationPolicy;
    private final CompositionAdaptationPolicy compositionAdaptationPolicy;
    private final NegotiationAdaptationPolicy negotiationAdaptationPolicy;
    private final ExecutionAdaptationPolicy executionAdaptationPolicy;
    
    private final ContinuousOrchestrationHandler continuousOrchestrationHandler;
    private final Collective collectiveforProvisioning;
    
    private TaskFlowDefinition(
        Set<LaborMode> laborMode,
        ImmutableList<ProvisioningHandler> provisioningHandlers,
        ImmutableList<CompositionHandler> compositionHandlers,
        ImmutableList<NegotiationHandler> negotiationHandlers,
        ContinuousOrchestrationHandler continuousOrchestrationHandler,
        ImmutableList<ExecutionHandler> executionHandlers,
        Collective collectiveforProvisioning) {
        this.laborMode = ImmutableSet.copyOf(laborMode);
        this.provisioningHandlers = ImmutableList.copyOf(provisioningHandlers);
        this.compositionHandlers = ImmutableList.copyOf(compositionHandlers);
        this.negotiationHandlers = ImmutableList.copyOf(negotiationHandlers);
        this.executionHandlers = ImmutableList.copyOf(executionHandlers);
        this.continuousOrchestrationHandler = continuousOrchestrationHandler;
        this.provisioningAdaptationPolicy = AdaptationPolicies.abort();
        this.compositionAdaptationPolicy = AdaptationPolicies.abort();
        this.negotiationAdaptationPolicy = AdaptationPolicies.abort();
        this.executionAdaptationPolicy = AdaptationPolicies.abort();
        this.collectiveforProvisioning = collectiveforProvisioning;
    }

    private TaskFlowDefinition(
        Set<LaborMode> laborMode,
        ImmutableList<ProvisioningHandler> provisioningHandlers,
        ImmutableList<CompositionHandler> compositionHandlers,
        ImmutableList<NegotiationHandler> negotiationHandlers,
        ContinuousOrchestrationHandler continuousOrchestrationHandler,
        ImmutableList<ExecutionHandler> executionHandlers,
        ProvisioningAdaptationPolicy provisioningAdaptationPolicy,
        CompositionAdaptationPolicy compositionAdaptationPolicy,
        NegotiationAdaptationPolicy negotiationAdaptationPolicy,
        ExecutionAdaptationPolicy executionAdaptationPolicy,
        Collective collectiveforProvisioning) {
        this.laborMode = ImmutableSet.copyOf(laborMode);
        this.provisioningHandlers = ImmutableList.copyOf(provisioningHandlers);
        this.compositionHandlers = ImmutableList.copyOf(compositionHandlers);
        this.negotiationHandlers = ImmutableList.copyOf(negotiationHandlers);
        this.executionHandlers = ImmutableList.copyOf(executionHandlers);
        this.continuousOrchestrationHandler = continuousOrchestrationHandler;
        this.collectiveforProvisioning = collectiveforProvisioning;
        this.provisioningAdaptationPolicy = provisioningAdaptationPolicy;
        this.compositionAdaptationPolicy = compositionAdaptationPolicy;
        this.negotiationAdaptationPolicy = negotiationAdaptationPolicy;
        this.executionAdaptationPolicy = executionAdaptationPolicy;
    }

    private TaskFlowDefinition() {
        laborMode = ImmutableSet.copyOf(EnumSet.noneOf(LaborMode.class));
        provisioningHandlers = null;
        compositionHandlers = null;
        negotiationHandlers = null;
        continuousOrchestrationHandler = null;
        executionHandlers = null;
        collectiveforProvisioning = null;
        provisioningAdaptationPolicy = null;
        compositionAdaptationPolicy = null;
        negotiationAdaptationPolicy = null;
        executionAdaptationPolicy = null;
    }

    /** Get the handler that will be used for provisioning in the CBT
     * @return handler for provisioning
     *
     * @exception IllegalStateException if provisioning handler has not been previously set
     */
    public ImmutableList<ProvisioningHandler> getProvisioningHandlers() {
        Preconditions.checkState(provisioningHandlers!=null, "Provisioning Handler not defined");
        return provisioningHandlers;
    }

    /* Get the handler used for composition.
     * @return handler for composition
     *
     * @exception IllegalStateException if composition handler has not been previously set
     * */
    public ImmutableList<CompositionHandler> getCompositionHandlers() {
        Preconditions.checkState(compositionHandlers != null, "Composition Handler not defined");
        return compositionHandlers;
    }

    /* Get the handler used for negotiation.
    * @return handler for negotiation
    *
    * @exception IllegalStateException if negotiation handler has not been previously set
    * */
    public ImmutableList<NegotiationHandler> getNegotiationHandlers() {
        Preconditions.checkState(negotiationHandlers != null, "Negotiation Handler not defined");
        return negotiationHandlers;
    }

    /* Get the handler used for execution.
    * @return handler for execution
    *
        * @exception IllegalStateException if execution handler has not been previously set
    * */
    public ImmutableList<ExecutionHandler> getExecutionHandlers() {
        Preconditions.checkState(executionHandlers != null, "Execution Handler not defined");
        return executionHandlers;
    }
    
    public ExecutionAdaptationPolicy getExecutionAdaptationPolicy() {
        Preconditions.checkState(executionAdaptationPolicy != null, "Execution Adaptation Policy not defined");
        return executionAdaptationPolicy;
    }

    /* Get the handler used for negotiation.
    * @return handler for negotiation
    *
    * @exception IllegalStateException if negotiation handler has not been previously set
    * */
    public ContinuousOrchestrationHandler getContinuousOrchestrationHandler() {
        Preconditions.checkState(continuousOrchestrationHandler != null,
                                 "Continuous Orchestration Handler not defined");
        return continuousOrchestrationHandler;
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
    public Optional<Collective> getCollectiveforProvisioning() {
        return Optional.ofNullable(collectiveforProvisioning);
    }

    /** Create the flow definition for a task that requires continuous orchestration
     *
     * @param continuousOrchestrationHandler
     * @return a TaskFlowDefinition with labor mode: {@link LaborMode#OPEN_CALL}
     */
    public static TaskFlowDefinition usingContinuousOrchestration(
        ContinuousOrchestrationHandler continuousOrchestrationHandler,
        ImmutableList<ExecutionHandler> executionHandlers) {
        Preconditions.checkNotNull(continuousOrchestrationHandler);
        EnumSet<LaborMode> laborMode = EnumSet.of(LaborMode.OPEN_CALL);
        return new TaskFlowDefinition(laborMode, null, null, null, continuousOrchestrationHandler,
                                      executionHandlers,
                                      null);
    }

    /** Create the flow definition for an on demand task with explicit composition
     *
     * @param provisioningHandlers the handler used for provisioning
     * @param compositionHandlers the handler used for composition
     * @param negotiationHandlers the negotiation used for negotiation
     * @return a TaskFlowDefinition with labor mode:  {@link LaborMode#OPEN_CALL},  {@link LaborMode#ON_DEMAND}
     */
    public static TaskFlowDefinition onDemandWithOpenCall(
        ImmutableList<ProvisioningHandler> provisioningHandlers,
        ImmutableList<CompositionHandler> compositionHandlers,
        ImmutableList<NegotiationHandler> negotiationHandlers,
        ImmutableList<ExecutionHandler> executionHandlers
    ) {
        Preconditions.checkNotNull(provisioningHandlers);
        Preconditions.checkNotNull(compositionHandlers);
        Preconditions.checkNotNull(negotiationHandlers);
        Preconditions.checkNotNull(executionHandlers);

        EnumSet<LaborMode> laborMode =
            EnumSet.of(LaborMode.ON_DEMAND, LaborMode.OPEN_CALL);
        return new TaskFlowDefinition(laborMode, provisioningHandlers, compositionHandlers, negotiationHandlers,
                                      null, executionHandlers,
                                      null);
    }

    /** Create the flow definition for an on demand task where no composition is allowed
     *
     * @param provisioningHandlers the handler used for provisioning
     * @param negotiationHandlers the negotiation used for negotiation
     * @return a TaskFlowDefinition with labor mode: {@link LaborMode#ON_DEMAND}
     */
    public static TaskFlowDefinition onDemandWithoutOpenCall(
        ImmutableList<ProvisioningHandler> provisioningHandlers,
        ImmutableList<NegotiationHandler> negotiationHandlers,
        ImmutableList<ExecutionHandler> executionHandlers
    ) {
        Preconditions.checkNotNull(provisioningHandlers);
        Preconditions.checkNotNull(negotiationHandlers);
        Preconditions.checkNotNull(executionHandlers);

        EnumSet<LaborMode> laborMode = EnumSet.of(LaborMode.ON_DEMAND);
        return new TaskFlowDefinition(laborMode, provisioningHandlers, null, negotiationHandlers, null, executionHandlers,
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
                continuousOrchestrationHandler, executionHandlers,
                provisioningAdaptationPolicy,
                compositionAdaptationPolicy,
                negotiationAdaptationPolicy,
                executionAdaptationPolicy,
                collectiveforProvisioning);
    }

    /** Creates a new instance with a new provisioning handler
     *
     * @param handler
     * @return an updated TaskFlowDefinition */
    public TaskFlowDefinition withProvisioningHandler(ImmutableList<ProvisioningHandler> handler) {
        Preconditions.checkNotNull(handler);
        return
            new TaskFlowDefinition(
                laborMode,
                handler,
                compositionHandlers,
                negotiationHandlers,
                continuousOrchestrationHandler, executionHandlers,
                provisioningAdaptationPolicy,
                compositionAdaptationPolicy,
                negotiationAdaptationPolicy,
                executionAdaptationPolicy,
                collectiveforProvisioning);
    }

    /** Creates a new instance with a new composition handler
     *
     * @param handler
     * @return an updated TaskFlowDefinition */
    public TaskFlowDefinition withCompositionHandler(ImmutableList<CompositionHandler> handler) {
        Preconditions.checkNotNull(handler);
        return
            new TaskFlowDefinition(
                laborMode,
                provisioningHandlers,
                handler,
                negotiationHandlers,
                continuousOrchestrationHandler, executionHandlers,
                provisioningAdaptationPolicy,
                compositionAdaptationPolicy,
                negotiationAdaptationPolicy,
                executionAdaptationPolicy,
                collectiveforProvisioning);
    }

    /** Creates a new instance with a new negotiation handler
     *
     * @param handler
     * @return an updated TaskFlowDefinition */
    public TaskFlowDefinition withNegotiationHandler(ImmutableList<NegotiationHandler> handler) {
        Preconditions.checkNotNull(handler);
        return
            new TaskFlowDefinition(
                laborMode,
                provisioningHandlers,
                compositionHandlers,
                handler,
                continuousOrchestrationHandler, 
                executionHandlers,
                provisioningAdaptationPolicy,
                compositionAdaptationPolicy,
                negotiationAdaptationPolicy,
                executionAdaptationPolicy,
                collectiveforProvisioning);
    }

    /** Creates a new instance with a new continuous orchestration handler
     *
     * @param handler
     * @return an updated TaskFlowDefinition */
    public TaskFlowDefinition withContinuousOrchestrationHandler(ContinuousOrchestrationHandler handler) {
        Preconditions.checkNotNull(handler);
        return
            new TaskFlowDefinition(
                laborMode,
                provisioningHandlers,
                compositionHandlers,
                negotiationHandlers,
                handler,
                executionHandlers,
                provisioningAdaptationPolicy,
                compositionAdaptationPolicy,
                negotiationAdaptationPolicy,
                executionAdaptationPolicy,
                collectiveforProvisioning);
    }

    /** Creates a new instance with a new execution handler
     *
     * @param handler
     * @return an updated TaskFlowDefinition */
    public TaskFlowDefinition withExecutionHandler(ImmutableList<ExecutionHandler> handler) {
        Preconditions.checkNotNull(handler);
        return
            new TaskFlowDefinition(
                laborMode,
                provisioningHandlers,
                compositionHandlers,
                negotiationHandlers,
                continuousOrchestrationHandler,
                handler,
                provisioningAdaptationPolicy,
                compositionAdaptationPolicy,
                negotiationAdaptationPolicy,
                executionAdaptationPolicy,
                collectiveforProvisioning);
    }
    
    public TaskFlowDefinition withExecutionAdaptationPolicy(ExecutionAdaptationPolicy policy) {
        Preconditions.checkNotNull(policy);
        return new TaskFlowDefinition(
                laborMode, 
                provisioningHandlers,
                compositionHandlers,
                negotiationHandlers,
                continuousOrchestrationHandler, 
                executionHandlers,
                provisioningAdaptationPolicy, 
                compositionAdaptationPolicy, 
                negotiationAdaptationPolicy, 
                policy, 
                collectiveforProvisioning);
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
                continuousOrchestrationHandler,
                executionHandlers,
                provisioningAdaptationPolicy,
                compositionAdaptationPolicy,
                negotiationAdaptationPolicy,
                executionAdaptationPolicy,
                collective);
    }

    /** Checks whether an instance is valid for being used in the construction of a
     * {@link CollectiveBasedTask#}
     *
     * @return true if the instance is valid, false otherwise */
    public boolean isValid() {
        if ( laborMode == null ) return false;

        if ( laborMode.contains(LaborMode.ON_DEMAND) ) {
            if ( provisioningHandlers == null || negotiationHandlers == null)
                return false;
        } else {
            return continuousOrchestrationHandler != null;
        }

        if ( laborMode.contains(LaborMode.OPEN_CALL) ) {
            return compositionHandlers != null;
        }

        return true;
    }
}
