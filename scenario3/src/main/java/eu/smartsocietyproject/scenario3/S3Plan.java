package eu.smartsocietyproject.pf;

public class S3Plan {
    private final String planId;

    public S3Plan(String planId) {
        this.planId = planId;
    }

    public String getPlanId() {
        return planId;
    }
}
