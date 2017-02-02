package eu.smartsocietyproject.scenario3;

import com.google.common.base.MoreObjects;
import eu.smartsocietyproject.pf.TaskDefinition;
import eu.smartsocietyproject.pf.TaskRequest;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

public class S3TaskRequest extends TaskRequest {
    private final String requiredSkill;
    private final String projectName;
    private final String description;
    private final Optional<String> group;
    private final long deadline;
    private final String token;
    private final double coverage;

    public S3TaskRequest(TaskDefinition definition, String type, Optional<String> defaultNamespace)
    {
        super(definition, type);
        this.requiredSkill = definition.getJson().get("required_skill").asText();
        this.projectName = definition.getJson().get("project_name").asText();
        this.description = definition.getJson().get("description").asText();
        this.token = definition.getJson().get("gitlab_token").asText();
        this.deadline =  Instant.now().plus(Duration.ofDays(1)).toEpochMilli();
        this.coverage =  definition.getJson().get("coverage").asDouble();

        Optional<String> requestGroup =
            Optional.ofNullable(definition.getJson().get("group")).map(jsonNode -> jsonNode.asText());

        this.group = requestGroup.isPresent()&&!requestGroup.get().trim().isEmpty()
                     ?requestGroup
                     :defaultNamespace;
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

    public long getDeadline() {
        return deadline;
    }

    public double getCoverage() {
        return coverage;
    }

    @Override
    public String getRequest() {
        return "";
    }


    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                                  .add("requiredSkill", requiredSkill)
                                  .add("projectName", projectName)
                                  .add("description", description)
                                  .add("group", group)
                                  .add("deadline", deadline)
                                  .add("token", token)
                                  .add("coverage", coverage)
                                  .toString();
    }
}
