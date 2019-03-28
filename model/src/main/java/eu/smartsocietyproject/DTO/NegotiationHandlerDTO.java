package eu.smartsocietyproject.DTO;

import com.google.common.collect.ImmutableList;
import eu.smartsocietyproject.pf.CollectiveWithPlan;

public class NegotiationHandlerDTO {
    private ImmutableList<CollectiveWithPlan> collectivesWithPlan;

    public NegotiationHandlerDTO(ImmutableList<CollectiveWithPlan> collectivesWithPlan) {
        this.collectivesWithPlan = collectivesWithPlan;
    }

    public ImmutableList<CollectiveWithPlan> getCollectivesWithPlan() {
        return collectivesWithPlan;
    }
}
