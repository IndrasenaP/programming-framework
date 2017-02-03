package eu.smartsocietyproject.pf;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import eu.smartsocietyproject.pf.adaptationPolicy.AbortPolicy;
import eu.smartsocietyproject.pf.adaptationPolicy.AdaptationPolicies;
import eu.smartsocietyproject.pf.adaptationPolicy.CompositionAdaptationPolicy;
import eu.smartsocietyproject.pf.adaptationPolicy.ExecutionAdaptationPolicy;
import eu.smartsocietyproject.pf.adaptationPolicy.NegotiationAdaptationPolicy;
import eu.smartsocietyproject.pf.adaptationPolicy.ProvisioningAdaptationPolicy;
import eu.smartsocietyproject.pf.cbthandlers.*;

import java.util.EnumSet;
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
 * <li>{@link #onDemandWithOpenCall(ProvisioningHandler, CompositionHandler, NegotiationHandler, ExecutionHandler)};</li>
 * <li>{@link #onDemandWithoutOpenCall(ProvisioningHandler, NegotiationHandler, ExecutionHandler)}</li>
 * <li>{@link #usingContinuousOrchestration(ContinuousOrchestrationHandler, ExecutionHandler)}</li>
 * </ul>
 *
 * makes it easier to create valid task flow definitions.
 *
 * This class is encapsulated by the {@link CBTBuilder} and used by {@link CollectiveBasedTask} for it's instantiation
 * and execution
 */
public class TaskFlowDefinition {
    private final ImmutableSet<CollectiveBasedTask.LaborMode> laborMode;
    
    private final ProvisioningHandler provisioningHandler;
    private final CompositionHandler compositionHandler;
    private final NegotiationHandler negotiationHandler;
    private final ExecutionHandler executionHandler;
    
    private final ProvisioningAdaptationPolicy provisioningAdaptationPolicy;
    private final CompositionAdaptationPolicy compositionAdaptationPolicy;
    private final NegotiationAdaptationPolicy negotiationAdaptationPolicy;
    private final ExecutionAdaptationPolicy executionAdaptationPolicy;
    
    private final ContinuousOrchestrationHandler continuousOrchestrationHandler;
    private final Collective collectiveforProvisioning;
    
    private TaskFlowDefinition(
        Set<CollectiveBasedTask.LaborMode> laborMode,
        ProvisioningHandler provisioningHandler,
        CompositionHandler compositionHandler,
        NegotiationHandler negotiationHandler,
        ContinuousOrchestrationHandler continuousOrchestrationHandler,
        ExecutionHandler executionHandler,
        Collective collectiveforProvisioning) {
        this.laborMode = ImmutableSet.copyOf(laborMode);
        this.provisioningHandler = provisioningHandler;
        this.compositionHandler = compositionHandler;
        this.negotiationHandler = negotiationHandler;
        this.executionHandler = executionHandler;
        this.continuousOrchestrationHandler = continuousOrchestrationHandler;
        this.provisioningAdaptationPolicy = AdaptationPolicies.abort();
        this.compositionAdaptationPolicy = AdaptationPolicies.abort();
        this.negotiationAdaptationPolicy = AdaptationPolicies.abort();
        this.executionAdaptationPolicy = AdaptationPolicies.abort();
        this.collectiveforProvisioning = collectiveforProvisioning;
    }

    private TaskFlowDefinition(
        Set<CollectiveBasedTask.LaborMode> laborMode,
        ProvisioningHandler provisioningHandler,
        CompositionHandler compositionHandler,
        NegotiationHandler negotiationHandler,
        ContinuousOrchestrationHandler continuousOrchestrationHandler,
        ExecutionHandler executionHandler,
        ProvisioningAdaptationPolicy provisioningAdaptationPolicy,
        CompositionAdaptationPolicy compositionAdaptationPolicy,
        NegotiationAdaptationPolicy negotiationAdaptationPolicy,
        ExecutionAdaptationPolicy executionAdaptationPolicy,
        Collective collectiveforProvisioning) {
        this.laborMode = ImmutableSet.copyOf(laborMode);
        this.provisioningHandler = provisioningHandler;
        this.compositionHandler = compositionHandler;
        this.negotiationHandler = negotiationHandler;
        this.executionHandler = executionHandler;
        this.continuousOrchestrationHandler = continuousOrchestrationHandler;
        this.collectiveforProvisioning = collectiveforProvisioning;
        this.provisioningAdaptationPolicy = provisioningAdaptationPolicy;
        this.compositionAdaptationPolicy = compositionAdaptationPolicy;
        this.negotiationAdaptationPolicy = negotiationAdaptationPolicy;
        this.executionAdaptationPolicy = executionAdaptationPolicy;
    }

    private TaskFlowDefinition() {
        laborMode = ImmutableSet.copyOf(EnumSet.noneOf(CollectiveBasedTask.LaborMode.class));
        provisioningHandler = null;
        compositionHandler = null;
        negotiationHandler = null;
        continuousOrchestrationHandler = null;
        executionHandler = null;
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
    public ProvisioningHandler getProvisioningHandler() {
        Preconditions.checkState(provisioningHandler!=null, "Provisioning Handler not defined");
        return provisioningHandler;
    }

    /* Get the handler used for composition.
     * @return handler for composition
     *
     * @exception IllegalStateException if composition handler has not been previously set
     * */
    public CompositionHandler getCompositionHandler() {
        Preconditions.checkState(compositionHandler != null, "Composition Handler not defined");
        return compositionHandler;
    }

    /* Get the handler used for negotiation.
    * @return handler for negotiation
    *
    * @exception IllegalStateException if negotiation handler has not been previously set
    * */
    public NegotiationHandler getNegotiationHandler() {
        Preconditions.checkState(negotiationHandler != null, "Negotiation Handler not defined");
        return negotiationHandler;
    }

    /* Get the handler used for execution.
    * @return handler for execution
    *
        * @exception IllegalStateException if execution handler has not been previously set
    * */
    public ExecutionHandler getExecutionHandler() {
        Preconditions.checkState(executionHandler != null, "Execution Handler not defined");
        return executionHandler;
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
    public ImmutableSet<CollectiveBasedTask.LaborMode> getLaborMode() {
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
     * @return a TaskFlowDefinition with labor mode: {@link CollectiveBasedTask.LaborMode#OPEN_CALL}
     */
    public static TaskFlowDefinition usingContinuousOrchestration(
        ContinuousOrchestrationHandler continuousOrchestrationHandler,
        ExecutionHandler executionHandler) {
        Preconditions.checkNotNull(continuousOrchestrationHandler);
        EnumSet<CollectiveBasedTask.LaborMode> laborMode = EnumSet.of(CollectiveBasedTask.LaborMode.OPEN_CALL);
        return new TaskFlowDefinition(laborMode, null, null, null, continuousOrchestrationHandler,
                                      executionHandler,
                                      null);
    }

    /** Create the flow definition for an on demand task with explicit composition
     *
     * @param provisioningHandler the handler used for provisioning
     * @param compositionHandler the handler used for composition
     * @param negotiationHandler the negotiation used for negotiation
     * @return a TaskFlowDefinition with labor mode:  {@link CollectiveBasedTask.LaborMode#OPEN_CALL},  {@link CollectiveBasedTask.LaborMode#ON_DEMAND}
     */
    public static TaskFlowDefinition onDemandWithOpenCall(
        ProvisioningHandler provisioningHandler,
        CompositionHandler compositionHandler,
        NegotiationHandler negotiationHandler,
        ExecutionHandler executionHandler
    ) {
        Preconditions.checkNotNull(provisioningHandler);
        Preconditions.checkNotNull(compositionHandler);
        Preconditions.checkNotNull(negotiationHandler);
        Preconditions.checkNotNull(executionHandler);

        EnumSet<CollectiveBasedTask.LaborMode> laborMode =
            EnumSet.of(CollectiveBasedTask.LaborMode.ON_DEMAND, CollectiveBasedTask.LaborMode.OPEN_CALL);
        return new TaskFlowDefinition(laborMode, provisioningHandler, compositionHandler, negotiationHandler,
                                      null, executionHandler,
                                      null);
    }

    /** Create the flow definition for an on demand task where no composition is allowed
     *
     * @param provisioningHandler the handler used for provisioning
     * @param negotiationHandler the negotiation used for negotiation
     * @return a TaskFlowDefinition with labor mode: {@link CollectiveBasedTask.LaborMode#ON_DEMAND}
     */
    public static TaskFlowDefinition onDemandWithoutOpenCall(
        ProvisioningHandler provisioningHandler,
        NegotiationHandler negotiationHandler,
        ExecutionHandler executionHandler
    ) {
        Preconditions.checkNotNull(provisioningHandler);
        Preconditions.checkNotNull(negotiationHandler);
        Preconditions.checkNotNull(executionHandler);

        EnumSet<CollectiveBasedTask.LaborMode> laborMode = EnumSet.of(CollectiveBasedTask.LaborMode.ON_DEMAND);
        return new TaskFlowDefinition(laborMode, provisioningHandler, null, negotiationHandler, null, executionHandler,
                                      null);
    }

    /** Create an empty task flow definition. Note that such definition will not be valid for any CBT
     *
     * @return an empty TaskFlowDefinition */
    public static TaskFlowDefinition empty() {
        return new TaskFlowDefinition();
    }

    /** Creates a new instance with {@link CollectiveBasedTask.LaborMode#OPEN_CALL} added to the labor mode
     *
     * @return an updated TaskFlowDefinition */
    public TaskFlowDefinition asOpenCall() {
        return withLaborMode(CollectiveBasedTask.LaborMode.OPEN_CALL);
    }

    /** Creates a new instance with {@link CollectiveBasedTask.LaborMode#ON_DEMAND} added to the labor mode
     *
     * @return an updated TaskFlowDefinition */
    public TaskFlowDefinition asOnDemand() {
        return withLaborMode(CollectiveBasedTask.LaborMode.ON_DEMAND);
    }

    private TaskFlowDefinition withLaborMode(CollectiveBasedTask.LaborMode lm) {
        Set<CollectiveBasedTask.LaborMode> ocSet = EnumSet.of(lm);

        return
            new TaskFlowDefinition(
                Sets.union(laborMode, ocSet),
                provisioningHandler,
                compositionHandler,
                negotiationHandler,
                continuousOrchestrationHandler, executionHandler,
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
    public TaskFlowDefinition withProvisioningHandler(ProvisioningHandler handler) {
        Preconditions.checkNotNull(handler);
        return
            new TaskFlowDefinition(
                laborMode,
                handler,
                compositionHandler,
                negotiationHandler,
                continuousOrchestrationHandler, executionHandler,
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
    public TaskFlowDefinition withCompositionHandler(CompositionHandler handler) {
        Preconditions.checkNotNull(handler);
        return
            new TaskFlowDefinition(
                laborMode,
                provisioningHandler,
                handler,
                negotiationHandler,
                continuousOrchestrationHandler, executionHandler,
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
    public TaskFlowDefinition withNegotiationHandler(NegotiationHandler handler) {
        Preconditions.checkNotNull(handler);
        return
            new TaskFlowDefinition(
                laborMode,
                provisioningHandler,
                compositionHandler,
                handler,
                continuousOrchestrationHandler, 
                executionHandler,
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
                provisioningHandler,
                compositionHandler,
                negotiationHandler,
                handler,
                executionHandler,
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
    public TaskFlowDefinition withExecutionHandler(ExecutionHandler handler) {
        Preconditions.checkNotNull(handler);
        return
            new TaskFlowDefinition(
                laborMode,
                provisioningHandler,
                compositionHandler,
                negotiationHandler,
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
                provisioningHandler, 
                compositionHandler, 
                negotiationHandler, 
                continuousOrchestrationHandler, 
                executionHandler, 
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
                provisioningHandler,
                compositionHandler,
                negotiationHandler,
                continuousOrchestrationHandler,
                executionHandler,
                provisioningAdaptationPolicy,
                compositionAdaptationPolicy,
                negotiationAdaptationPolicy,
                executionAdaptationPolicy,
                collective);
    }

    /** Checks whether an instance is valid for being used in the construction of a
     * {@link eu.smartsocietyproject.pf.CollectiveBasedTask}
     *
     * @return true if the instance is valid, false otherwise */
    public boolean isValid() {
        if ( laborMode == null ) return false;

        if ( laborMode.contains(CollectiveBasedTask.LaborMode.ON_DEMAND) ) {
            if ( provisioningHandler == null || negotiationHandler == null)
                return false;
        } else {
            return continuousOrchestrationHandler != null;
        }

        if ( laborMode.contains(CollectiveBasedTask.LaborMode.OPEN_CALL) ) {
            return compositionHandler != null;
        }

        return true;
    }
}
