package eu.smartsocietyproject.pf;

import akka.actor.Props;
import com.google.common.base.Preconditions;
import eu.smartsocietyproject.pf.enummerations.*;
import eu.smartsocietyproject.pf.CollectiveBasedTask;
import eu.smartsocietyproject.pf.TaskFlowDefinition;

import java.util.List;

/**
 * This class provide the tool for creating a {@link CollectiveBasedTask}. It encapsulate a {@link TaskFlowDefinition}
 * and most of the methods are used for setting up such definition.
 *
 * To create a {@link CollectiveBasedTask} one needs to create a valid definition and to specify a request. A Builder
 * can be used only after it has been registered into a {@link ApplicationContext} through the method
 * {@link ApplicationContext#registerBuilderForCBTType(String, CBTBuilder)}.
 *
 * The class is immutable and designed with a fluent interface.
 *
 *
 */
public class CBTBuilder {
    private ApplicationContext context;
    private final TaskFlowDefinition definition;
    private final TaskRequest request;

    private CBTBuilder(
        ApplicationContext context,
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

    CBTBuilder registerToContext(ApplicationContext ctx) {
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

    /** Add {@link LaborMode#ON_DEMAND} to the definition's labor mode
     *
     * @return a CBTBuilder with the definition changed accordingly */
    public CBTBuilder asOnDemand() {
        return new CBTBuilder(context, definition.asOnDemand(), request);
    }

    /** Add {@link LaborMode#OPEN_CALL} to the definition's labor mode
     *
     *
     * @return a CBTBuilder with the definition changed accordingly */
    public CBTBuilder asOpenCall() {
        return new CBTBuilder(context, definition.asOpenCall(), request);
    }

    /** Changes provisioning handler in the definition
     *
     * @param handlers provisioning handler
     * @return a CBTBuilder with the definition changed accordingly */
    public CBTBuilder withProvisioningHandlers(List<Props> handlers) {
        Preconditions.checkNotNull(handlers);
        return new CBTBuilder(context, definition.withProvisioningHandlers(handlers), request);
    }

    /** Changes provisioning handler in the definition
     *
     * @param handlers provisioning handler
     * @return a CBTBuilder with the definition changed accordingly */
    public CBTBuilder withExecutionHandlers(List<Props> handlers) {
        Preconditions.checkNotNull(handlers);
        return new CBTBuilder(context, definition.withExecutionHandlers(handlers), request);
    }

    /** Changes composition handler in the definition
     *
     * @param handlers composition handler
     * @return a CBTBuilder with the definition changed accordingly */
    public CBTBuilder withCompositionHandlers(List<Props> handlers) {
        Preconditions.checkNotNull(handlers);
        return new CBTBuilder(context, definition.withCompositionHandlers(handlers), request);
    }

    /** Changes negotiation handler in the definition
     *
     * @param handlers negotiation handler
     * @return a CBTBuilder with the negotiation changed accordingly */
    public CBTBuilder withNegotiationHandlers(List<Props> handlers) {
        Preconditions.checkNotNull(handlers);
        return new CBTBuilder(context, definition.withNegotiationHandlers(handlers), request);
    }

    /** Changes negotiation handler in the definition
     *
     * @param handlers continuous orchestration handlers
     * @return a CBTBuilder with the negotiation changed accordingly */
    public CBTBuilder withContinuousOrchestrationHandlers(List<Props> handlers) {
        Preconditions.checkNotNull(handlers);
        return new CBTBuilder(context, definition.withContinuousOrchestrationHandlers(handlers), request);
    }

    /** Changes quality assurance handlers in the definition
     *
     * @param handlers quality assurance handlers
     * @return a CBTBuilder with the negotiation changed accordingly */
    public CBTBuilder withQualityAssuranceHandlers(List<Props> handlers) {
        Preconditions.checkNotNull(handlers);
        return new CBTBuilder(context, definition.withQualityAssuranceHandlers(handlers), request);
    }

    /** Assign a request to the builder
     *
     *  @param rqst the request to be assigned
     *  @return a CBTBuilder with the assigned request
     */
    public CBTBuilder withTaskRequest(TaskRequest rqst) {
        return new CBTBuilder(context, definition, rqst);
    }

    /** Assign a request to the builder
     *
     *  @param collective the request to be assigned
     *  @return a CBTBuilder with the assigned request
     */
    public CBTBuilder withInputCollective(Collective collective) {
        return new CBTBuilder(context, definition.withCollectiveForProvisioning(collective), request);
    }

    /** {@link CollectiveBasedTask} building method, prerequisites:
     *  <ul>
     *      <li>the definition must be valid</li>
     *      <li>Request must be set</li>
     *      <li>CBTBuilder must have been registered to a context through the
     *      {@link ApplicationContext#registerBuilderForCBTType(String, CBTBuilder)} method</li>
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
