package eu.smartsocietyproject.scenario4;

import eu.smartsocietyproject.pf.TaskDefinition;
import eu.smartsocietyproject.pf.TaskRequest;

import java.util.Optional;

public class S4TaskRequest extends TaskRequest {
    private final String requiredSkill;
    private final String projectName;
    private final String description;
    private final Optional<String> group;
    private final String token;

    public S4TaskRequest(TaskDefinition definition, String type, Optional<String> defaultNamespace)
    {
        super(definition, type);
        this.requiredSkill = definition.getJson().get("required_skill").asText();
        this.projectName = definition.getJson().get("project_name").asText();
        this.description = definition.getJson().get("description").asText();
        this.token = definition.getJson().get("gitlab_token").asText();
        Optional<String> requestGroup =
            Optional.ofNullable(definition.getJson().get("group")).map(jsonNode -> jsonNode.asText());

        this.group = requestGroup.isPresent()?requestGroup:defaultNamespace;
    }

    public String getRequiredSkill() {
        return requiredSkill;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getDescription() {
        return description;
    }

    public Optional<String> getGroup() {
        return group;
    }

    public String getToken() {
        return token;
    }

    @Override
    public String getRequest() {
        return "";
    }
}
