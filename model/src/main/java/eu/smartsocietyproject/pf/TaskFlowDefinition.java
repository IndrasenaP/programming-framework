package eu.smartsocietyproject.pf;

import akka.actor.ActorRef;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import eu.smartsocietyproject.pf.cbthandlers.*;
import eu.smartsocietyproject.pf.enummerations.LaborMode;

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
    private final ImmutableSet<LaborMode> laborMode;
    private final ActorRef provisioningHandler;
    private final ActorRef compositionHandler;
    private final ActorRef negotiationHandler;
    private final ActorRef executionHandler;
    private final ContinuousOrchestrationHandler continuousOrchestrationHandler;
    private final Collective collectiveForProvisioning;

    private TaskFlowDefinition(
            Set<LaborMode> laborMode,
            ActorRef provisioningHandler,
            ActorRef compositionHandler,
            ActorRef negotiationHandler,
            ContinuousOrchestrationHandler continuousOrchestrationHandler,
            ActorRef executionHandler,
            Collective collectiveForProvisioning) {
        this.laborMode = ImmutableSet.copyOf(laborMode);
        this.provisioningHandler = provisioningHandler;
        this.compositionHandler = compositionHandler;
        this.negotiationHandler = negotiationHandler;
        this.executionHandler = executionHandler;
        this.continuousOrchestrationHandler = continuousOrchestrationHandler;
        this.collectiveForProvisioning = collectiveForProvisioning;
    }

    private TaskFlowDefinition() {
        laborMode = ImmutableSet.copyOf(EnumSet.noneOf(LaborMode.class));
        provisioningHandler = null;
        compositionHandler = null;
        negotiationHandler = null;
        continuousOrchestrationHandler = null;
        executionHandler = null;
        collectiveForProvisioning = null;
    }

    /** Get the handler that will be used for provisioning in the CBT
     * @return handler for provisioning
     *
     * @exception IllegalStateException if provisioning handler has not been previously set
     */
    public ActorRef getProvisioningHandler() {
        Preconditions.checkState(provisioningHandler!=null, "Provisioning Handler not defined");
        return provisioningHandler;
    }

    /* Get the handler used for composition.
     * @return handler for composition
     *
     * @exception IllegalStateException if composition handler has not been previously set
     * */
    public ActorRef getCompositionHandler() {
        Preconditions.checkState(compositionHandler != null, "Composition Handler not defined");
        return compositionHandler;
    }

    /* Get the handler used for negotiation.
     * @return handler for negotiation
     *
     * @exception IllegalStateException if negotiation handler has not been previously set
     * */
    public ActorRef getNegotiationHandler() {
        Preconditions.checkState(negotiationHandler != null, "Negotiation Handler not defined");
        return negotiationHandler;
    }

    /* Get the handler used for execution.
     * @return handler for execution
     *
     * @exception IllegalStateException if execution handler has not been previously set
     * */
    public ActorRef getExecutionHandler() {
        Preconditions.checkState(executionHandler != null, "Execution Handler not defined");
        return executionHandler;
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
    public Optional<Collective> getCollectiveForProvisioning() {
        return Optional.ofNullable(collectiveForProvisioning);
    }

    /** Create the flow definition for a task that requires continuous orchestration
     *
     * @param continuousOrchestrationHandler
     * @return a TaskFlowDefinition with labor mode: {@link LaborMode#OPEN_CALL}
     */
    public static TaskFlowDefinition usingContinuousOrchestration(
            ContinuousOrchestrationHandler continuousOrchestrationHandler,
            ActorRef executionHandler) {
        Preconditions.checkNotNull(continuousOrchestrationHandler);
        EnumSet<LaborMode> laborMode = EnumSet.of(LaborMode.OPEN_CALL);
        return new TaskFlowDefinition(laborMode, null, null, null, continuousOrchestrationHandler,
                executionHandler,
                null);
    }

    /** Create the flow definition for an on demand task with explicit composition
     *
     * @param provisioningHandler the handler used for provisioning
     * @param compositionHandler the handler used for composition
     * @param negotiationHandler the negotiation used for negotiation
     * @return a TaskFlowDefinition with labor mode:  {@link LaborMode#OPEN_CALL},  {@link LaborMode#ON_DEMAND}
     */
    public static TaskFlowDefinition onDemandWithOpenCall(
            ActorRef provisioningHandler,
            ActorRef compositionHandler,
            ActorRef negotiationHandler,
            ActorRef executionHandler
    ) {
        Preconditions.checkNotNull(provisioningHandler);
        Preconditions.checkNotNull(compositionHandler);
        Preconditions.checkNotNull(negotiationHandler);
        Preconditions.checkNotNull(executionHandler);

        EnumSet<LaborMode> laborMode =
                EnumSet.of(LaborMode.ON_DEMAND, LaborMode.OPEN_CALL);
        return new TaskFlowDefinition(laborMode, provisioningHandler, compositionHandler, negotiationHandler,
                null, executionHandler,
                null);
    }

    /** Create the flow definition for an on demand task where no composition is allowed
     *
     * @param provisioningHandler the handler used for provisioning
     * @param negotiationHandler the negotiation used for negotiation
     * @return a TaskFlowDefinition with labor mode: {@link LaborMode#ON_DEMAND}
     */
    public static TaskFlowDefinition onDemandWithoutOpenCall(
            ActorRef provisioningHandler,
            ActorRef negotiationHandler,
            ActorRef executionHandler
    ) {
        Preconditions.checkNotNull(provisioningHandler);
        Preconditions.checkNotNull(negotiationHandler);
        Preconditions.checkNotNull(executionHandler);

        EnumSet<LaborMode> laborMode = EnumSet.of(LaborMode.ON_DEMAND);
        return new TaskFlowDefinition(laborMode, provisioningHandler, null, negotiationHandler, null, executionHandler,
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
                        provisioningHandler,
                        compositionHandler,
                        negotiationHandler,
                        continuousOrchestrationHandler, executionHandler,
                        collectiveForProvisioning);
    }

    /** Creates a new instance with a new provisioning handler
     *
     * @param handler
     * @return an updated TaskFlowDefinition */
    public TaskFlowDefinition withProvisioningHandler(ActorRef handler) {
        Preconditions.checkNotNull(handler);
        return
                new TaskFlowDefinition(
                        laborMode,
                        handler,
                        compositionHandler,
                        negotiationHandler,
                        continuousOrchestrationHandler, executionHandler,
                        collectiveForProvisioning);
    }

    /** Creates a new instance with a new composition handler
     *
     * @param handler
     * @return an updated TaskFlowDefinition */
    public TaskFlowDefinition withCompositionHandler(ActorRef handler) {
        Preconditions.checkNotNull(handler);
        return
                new TaskFlowDefinition(
                        laborMode,
                        provisioningHandler,
                        handler,
                        negotiationHandler,
                        continuousOrchestrationHandler, executionHandler,
                        collectiveForProvisioning);
    }

    /** Creates a new instance with a new negotiation handler
     *
     * @param handler
     * @return an updated TaskFlowDefinition */
    public TaskFlowDefinition withNegotiationHandler(ActorRef handler) {
        Preconditions.checkNotNull(handler);
        return
                new TaskFlowDefinition(
                        laborMode,
                        provisioningHandler,
                        compositionHandler,
                        handler,
                        continuousOrchestrationHandler, executionHandler,
                        collectiveForProvisioning);
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
                        collectiveForProvisioning);
    }

    /** Creates a new instance with a new execution handler
     *
     * @param handler
     * @return an updated TaskFlowDefinition */
    public TaskFlowDefinition withExecutionHandler(ActorRef handler) {
        Preconditions.checkNotNull(handler);
        return
                new TaskFlowDefinition(
                        laborMode,
                        provisioningHandler,
                        compositionHandler,
                        negotiationHandler,
                        continuousOrchestrationHandler,
                        handler,
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
                        provisioningHandler,
                        compositionHandler,
                        negotiationHandler,
                        continuousOrchestrationHandler,
                        executionHandler,
                        collective);
    }

    /** Checks whether an instance is valid for being used in the construction of a
     * {@link eu.smartsocietyproject.pf.CollectiveBasedTask}
     *
     * @return true if the instance is valid, false otherwise */
    public boolean isValid() {
        if ( laborMode == null ) return false;

        if ( laborMode.contains(LaborMode.ON_DEMAND) ) {
            if ( provisioningHandler == null || negotiationHandler == null)
                return false;
        } else {
            return continuousOrchestrationHandler != null;
        }

        if ( laborMode.contains(LaborMode.OPEN_CALL) ) {
            return compositionHandler != null;
        }

        return true;
    }
}
