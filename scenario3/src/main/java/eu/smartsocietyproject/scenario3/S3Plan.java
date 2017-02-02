package eu.smartsocietyproject.scenario3;

import eu.smartsocietyproject.pf.Plan;

public class S3Plan extends Plan {
    private final String planId;
    private final S3TaskRequest request;

    public S3Plan(String planId, S3TaskRequest request) {
        this.planId = planId;
        this.request = request;
    }

    public String getPlanId() {
        return planId;
    }

    public S3TaskRequest getRequest() {
        return request;
    }

}
