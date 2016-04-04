package eu.smartsocietyproject.pf.orchestration;

import eu.smartsocietyproject.pf.Collective;
import eu.smartsocietyproject.pf.Plan;

public class CollectiveWithPlan {
    private final Collective collective;
    private final Plan plan;

    private CollectiveWithPlan(Collective collective, Plan plan) {
        this.collective = collective;
        this.plan = plan;
    }

    public static CollectiveWithPlan of(Collective collective, Plan plan) {
        return new CollectiveWithPlan(collective, plan);
    }

    public Collective getCollective() {
        return collective;
    }

    public Plan getPlan() {
        return plan;
    }
}
