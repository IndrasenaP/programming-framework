package eu.smartsocietyproject.pf;

import java.util.UUID;

public abstract class TaskRequest {
    private final TaskDefinition definition;
    private final String type;

    public TaskRequest(TaskDefinition definition, String type) {
        this.definition = definition;
        this.type = type;
    }

    public abstract String getRequest();

    public TaskDefinition getDefinition() {
        return definition;
    }

    public UUID getId() {
        return definition.getId();
    }

    public String getType() {
        return type;
    }
}
