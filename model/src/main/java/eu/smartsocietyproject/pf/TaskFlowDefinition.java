package eu.smartsocietyproject.pf;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import eu.smartsocietyproject.pf.cbthandlers.CompositionHandler;
import eu.smartsocietyproject.pf.cbthandlers.ContinuousOrchestrationHandler;
import eu.smartsocietyproject.pf.cbthandlers.NegotiationHandler;
import eu.smartsocietyproject.pf.cbthandlers.ProvisioningHandler;

import java.util.EnumSet;
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
 * <li>{@link #onDemandWithOpenCall(ProvisioningHandler, CompositionHandler, NegotiationHandler)};</li>
 * <li>{@link #onDemandWithoutOpenCall(ProvisioningHandler, NegotiationHandler)}</li>
 * <li>{@link #usingContinuousOrchestration(ContinuousOrchestrationHandler)}</li>
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
    private final ContinuousOrchestrationHandler continuousOrchestrationHandler;

    private TaskFlowDefinition(
        Set<CollectiveBasedTask.LaborMode> laborMode,
        ProvisioningHandler provisioningHandler,
        CompositionHandler compositionHandler,
        NegotiationHandler negotiationHandler,
        ContinuousOrchestrationHandler continuousOrchestrationHandler) {
        this.laborMode = ImmutableSet.copyOf(laborMode);
        this.provisioningHandler = provisioningHandler;
        this.compositionHandler = compositionHandler;
        this.negotiationHandler = negotiationHandler;
        this.continuousOrchestrationHandler = continuousOrchestrationHandler;
    }

    private TaskFlowDefinition() {
        laborMode = ImmutableSet.copyOf(EnumSet.noneOf(CollectiveBasedTask.LaborMode.class));
        provisioningHandler = null;
        compositionHandler = null;
        negotiationHandler = null;
        continuousOrchestrationHandler = null;
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
        if (compositionHandler == null)
            throw new IllegalStateException("Composition Handler not defined");
        return compositionHandler;
    }

    /* Get the handler used for negotiation.
    * @return handler for negotiation
    *
    * @exception IllegalStateException if negotiation handler has not been previously set
    * */
    public NegotiationHandler getNegotiationHandler() {
        if (negotiationHandler == null)
            throw new IllegalStateException("Negotiation Handler not defined");
        return negotiationHandler;
    }

    /* Get the handler used for negotiation.
    * @return handler for negotiation
    *
    * @exception IllegalStateException if negotiation handler has not been previously set
    * */
    public ContinuousOrchestrationHandler getContinuousOrchestrationHandler() {
        if (continuousOrchestrationHandler == null)
            throw new IllegalStateException("Continuous Orchestration Handler not defined");
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


    /** Create the flow definition for a task that requires continuous orchestration
     *
     * @param continuousOrchestrationHandler
     * @return a TaskFlowDefinition with labor mode: {@link CollectiveBasedTask.LaborMode#OPEN_CALL}
     */
    public static TaskFlowDefinition usingContinuousOrchestration(ContinuousOrchestrationHandler continuousOrchestrationHandler) {
        Preconditions.checkNotNull(continuousOrchestrationHandler);
        EnumSet<CollectiveBasedTask.LaborMode> laborMode = EnumSet.of(CollectiveBasedTask.LaborMode.OPEN_CALL);
        return new TaskFlowDefinition(laborMode, null, null, null, continuousOrchestrationHandler);
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
        NegotiationHandler negotiationHandler
    ) {
        Preconditions.checkNotNull(provisioningHandler);
        Preconditions.checkNotNull(compositionHandler);
        Preconditions.checkNotNull(negotiationHandler);

        EnumSet<CollectiveBasedTask.LaborMode> laborMode =
            EnumSet.of(CollectiveBasedTask.LaborMode.ON_DEMAND, CollectiveBasedTask.LaborMode.OPEN_CALL);
        return new TaskFlowDefinition(laborMode, provisioningHandler, compositionHandler, negotiationHandler, null);
    }

    /** Create the flow definition for an on demand task where no composition is allowed
     *
     * @param provisioningHandler the handler used for provisioning
     * @param negotiationHandler the negotiation used for negotiation
     * @return a TaskFlowDefinition with labor mode: {@link CollectiveBasedTask.LaborMode#ON_DEMAND}
     */
    public static TaskFlowDefinition onDemandWithoutOpenCall(
        ProvisioningHandler provisioningHandler,
        NegotiationHandler negotiationHandler
    ) {
        Preconditions.checkNotNull(provisioningHandler);
        Preconditions.checkNotNull(negotiationHandler);

        EnumSet<CollectiveBasedTask.LaborMode> laborMode = EnumSet.of(CollectiveBasedTask.LaborMode.ON_DEMAND);
        return new TaskFlowDefinition(laborMode, provisioningHandler, null, negotiationHandler, null);
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
                continuousOrchestrationHandler
            );
    }

    /** Creates a new instance with a new provisioning handler
     *
     * @param handler
     * @return an updated TaskFlowDefinition */
    public TaskFlowDefinition withProvisioningHandler(ProvisioningHandler handler) {
        return
            new TaskFlowDefinition(
                laborMode,
                handler,
                compositionHandler,
                negotiationHandler,
                continuousOrchestrationHandler
            );
    }

    /** Creates a new instance with a new composition handler
     *
     * @param handler
     * @return an updated TaskFlowDefinition */
    public TaskFlowDefinition withCompositionHandler(CompositionHandler handler) {
        return
            new TaskFlowDefinition(
                laborMode,
                provisioningHandler,
                handler,
                negotiationHandler,
                continuousOrchestrationHandler
            );
    }

    /** Creates a new instance with a new negotiation handler
     *
     * @param handler
     * @return an updated TaskFlowDefinition */
    public TaskFlowDefinition withNegotiationHandler(NegotiationHandler handler) {
        return
            new TaskFlowDefinition(
                laborMode,
                provisioningHandler,
                compositionHandler,
                handler,
                continuousOrchestrationHandler
            );
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
