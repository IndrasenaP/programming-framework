package eu.smartsocietyproject.pf.orchestration;

import com.google.common.base.Objects;
import eu.smartsocietyproject.pf.CollectiveBase;
import eu.smartsocietyproject.pf.Plan;

public class CollectiveWithPlan {
    private final CollectiveBase collective;
    private final Plan plan;

    private CollectiveWithPlan(CollectiveBase collective, Plan plan) {
        this.collective = collective;
        this.plan = plan;
    }

    public static CollectiveWithPlan of(CollectiveBase collective, Plan plan) {
        return new CollectiveWithPlan(collective, plan);
    }

    public CollectiveBase getCollective() {
        return collective;
    }

    public Plan getPlan() {
        return plan;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CollectiveWithPlan that = (CollectiveWithPlan) o;

        return Objects.equal(this.collective, that.collective) &&
            Objects.equal(this.plan, that.plan);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(collective, plan);
    }
}
