package eu.smartsocietyproject.pf;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;

import java.util.UUID;

public class TaskDefinition {
    private final UUID id=UUID.randomUUID();
        private final JsonNode json;

    public TaskDefinition(JsonNode json) {
        Preconditions.checkNotNull(json);
        this.json = json;
    }

    public UUID getId() {
        return id;
    }

    public JsonNode getJson() {
        return json;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                          .add("id", id)
                          .add("json", json)
                          .toString();
    }
}
