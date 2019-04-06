package eu.smartsocietyproject.DTO;

public class ResultDTO {
    private double qor;
    private String result;

    public ResultDTO(double qor, String result) {
        this.qor = qor;
        this.result = result;
    }

    public String getResult() {
        return result;
    }

    public double getQor() {
        return qor;
    }
}
