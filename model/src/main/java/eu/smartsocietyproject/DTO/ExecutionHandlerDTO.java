package eu.smartsocietyproject.DTO;

import eu.smartsocietyproject.pf.CollectiveWithPlan;

public class ExecutionHandlerDTO {
    private CollectiveWithPlan collectiveWithPlan;

    public ExecutionHandlerDTO(CollectiveWithPlan collectiveWithPlan) {
        this.collectiveWithPlan = collectiveWithPlan;
    }

    public CollectiveWithPlan getCollectiveWithPlan() {
        return collectiveWithPlan;
    }
}
