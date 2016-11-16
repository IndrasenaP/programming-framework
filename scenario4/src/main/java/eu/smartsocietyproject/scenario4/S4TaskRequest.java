package eu.smartsocietyproject.scenario4;

import eu.smartsocietyproject.pf.TaskDefinition;
import eu.smartsocietyproject.pf.TaskRequest;

public class S4TaskRequest extends TaskRequest {
    private final String requiredSkill;

    public S4TaskRequest(TaskDefinition definition, String type, String requiredSkill)
    {
        super(definition, type);
        this.requiredSkill = requiredSkill;
    }

    public String getRequiredSkill() {
        return requiredSkill;
    }

    @Override
    public String getRequest() {
        return "";
    }
}
