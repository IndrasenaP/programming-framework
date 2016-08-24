package eu.smartsocietyproject.pf;

import com.google.common.base.Preconditions;
import eu.smartsocietyproject.pf.cbthandlers.CompositionHandler;
import eu.smartsocietyproject.pf.cbthandlers.NegotiationHandler;
import eu.smartsocietyproject.pf.cbthandlers.ProvisioningHandler;

/**
 * This class provide the tool for creating a {@link CollectiveBasedTask}. It encapsulate a {@link TaskFlowDefinition}
 * and most of the methods are used for setting up such definition.
 *
 * To create a {@link CollectiveBasedTask} one needs to create a valid definition and to specify a request. A Builder
 * can be used only after it has been registered into a {@link SmartSocietyApplicationContext} through the method
 * {@link SmartSocietyApplicationContext#registerBuilderForCBTType(String, CBTBuilder)}.
 *
 * The class is immutable and designed with a fluent interface.
 *
 *
 */
public class CBTBuilder {
    private SmartSocietyApplicationContext context;
    private final TaskFlowDefinition definition;
    private final TaskRequest request;

    private CBTBuilder(
        SmartSocietyApplicationContext context,
        TaskFlowDefinition definition,
        TaskRequest request) {
        this.context = context;
        this.definition = definition;
        this.request = request;
    }

    private CBTBuilder() {
        this.context = null;
        this.definition = TaskFlowDefinition.empty();
        this.request = null;
    }

    CBTBuilder registerToContext(SmartSocietyApplicationContext ctx) {
        Preconditions.checkNotNull(ctx);
        Preconditions.checkState(context == null, "Cannot change the builder context");

        return new CBTBuilder(ctx, definition, request);
    }

    /**
     * Creates an empty CBTBuilder
     *
     * @return an empty CBTBuilder
     */
    public static CBTBuilder empty() {
        return new CBTBuilder();
    }

    /**
     * Create a CBTBuilder based on a given definition, the definition can also be partial, but the builder must
     * have a valid definition before the {@link #build()} method is called.
     *
     * @return a CBTBuilder with the specified definition
     */
    public static CBTBuilder from(TaskFlowDefinition definition) {
        Preconditions.checkNotNull(definition);
        return new CBTBuilder(null, definition, null);
    }

    /** Add {@link CollectiveBasedTask.LaborMode#ON_DEMAND} to the definition's labor mode
     *
     * @return a CBTBuilder with the definition changed accordingly */
    public CBTBuilder asOnDemand() {
        return new CBTBuilder(context, definition.asOnDemand(), request);
    }

    /** Add {@link CollectiveBasedTask.LaborMode#OPEN_CALL} to the definition's labor mode
     *
     *
     * @return a CBTBuilder with the definition changed accordingly */
    public CBTBuilder asOpenCall() {
        return new CBTBuilder(context, definition.asOpenCall(), request);
    }

    /** Changes provisioning handler in the definition
     *
     * @param handler provisioning handler
     * @return a CBTBuilder with the definition changed accordingly */
    public CBTBuilder withProvisioningHandler(ProvisioningHandler handler) {
        Preconditions.checkNotNull(handler);
        return new CBTBuilder(context, definition.withProvisioningHandler(handler), request);
    }

    /** Changes composition handler in the definition
     *
     * @param handler composition handler
     * @return a CBTBuilder with the definition changed accordingly */
    public CBTBuilder withCompositionHandler(CompositionHandler handler) {
        Preconditions.checkNotNull(handler);
        return new CBTBuilder(context, definition.withCompositionHandler(handler), request);
    }

    /** Changes negotiation handler in the definition
     *
     * @param handler negotiation handler
     * @return a CBTBuilder with the negotiation changed accordingly */
    public CBTBuilder withNegotiationHandler(NegotiationHandler handler) {
        Preconditions.checkNotNull(handler);
        return new CBTBuilder(context, definition.withNegotiationHandler(handler), request);
    }

    /** Assign a request to the builder
     *
     *  @param rqst the request to be assigned
     *  @return a CBTBuilder with the assigned request
     */
    public CBTBuilder withTaskRequest(TaskRequest rqst) {
        return new CBTBuilder(context, definition, rqst);
    }

    /** {@link CollectiveBasedTask} building method, prerequisites:
     *  <ul>
     *      <li>the definition must be valid</li>
     *      <li>Request must be set</li>
     *      <li>CBTBuilder must have been registered to a context through the
     *      {@link SmartSocietyApplicationContext#registerBuilderForCBTType(String, CBTBuilder)} method</li>
     *  </ul>
     * @exception IllegalStateException one of the prerequisites is not satisfied
     * @return a CBTBuilder with the negotiation changed accordingly */
    public CollectiveBasedTask build() {
        Preconditions.checkState(context != null, "Builder has not been associated to any context");
        Preconditions.checkState(request != null, "Builder has not been associated to any request");
        Preconditions.checkState(definition.isValid(), "Builder definition is not valid");

        return CollectiveBasedTask.create(context, request, definition);
    }

}
