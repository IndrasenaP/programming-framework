package eu.smartsocietyproject.pf;

public class CBTBuilder {
    private final SmartSocietyApplicationContext context;
    private final TaskFlowDefinition definition;
    private final TaskRequest request;

    public CBTBuilder(
        SmartSocietyApplicationContext context,
        TaskFlowDefinition definition,
        TaskRequest request) {
        this.context = context;
        this.definition = definition;
        this.request = request;
    }

    public CollectiveBasedTask build() {
        return CollectiveBasedTask.create(context, request, definition);
    }

}
