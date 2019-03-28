package eu.smartsocietyproject.DTO;

public class IncentivizeDTO {
    private String incentiveType;
    private Object incentiveSpecificParams;

    public IncentivizeDTO(String incentiveType, Object incentiveSpecificParams) {
        this.incentiveType = incentiveType;
        this.incentiveSpecificParams = incentiveSpecificParams;
    }

    public String getIncentiveType() {
        return incentiveType;
    }

    public Object getIncentiveSpecificParams() {
        return incentiveSpecificParams;
    }
}
