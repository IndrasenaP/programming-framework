package eu.smartsocietyproject.pf;

import eu.smartsocietyproject.pf.cbthandlers.CompositionHandler;
import eu.smartsocietyproject.pf.cbthandlers.NegotiationHandler;

public class TaskDescriptor {
    private final CompositionHandler compositionHandler;
    private final NegotiationHandler negotiationHandler;
    private final TaskRequest request;

    public TaskDescriptor(
        CompositionHandler compositionHandler,
        NegotiationHandler negotiationHandler, TaskRequest request) {
        this.compositionHandler = compositionHandler;
        this.negotiationHandler = negotiationHandler;
        this.request = request;
    }

    public CompositionHandler getCompositionHandler() {
        return compositionHandler;
    }

    public NegotiationHandler getNegotiationHandler() {
        return negotiationHandler;
    }

    public TaskRequest getRequest() {
        return request;
    }
}
