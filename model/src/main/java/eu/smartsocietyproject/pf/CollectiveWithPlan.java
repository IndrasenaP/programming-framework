package eu.smartsocietyproject.pf;

import com.google.common.base.Objects;

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
