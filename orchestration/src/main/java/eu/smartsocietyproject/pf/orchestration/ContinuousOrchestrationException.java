package eu.smartsocietyproject.pf.orchestration;

public class ContinuousOrchestrationException extends Exception {
    public ContinuousOrchestrationException() {
    }

    public ContinuousOrchestrationException(String message) {
        super(message);
    }

    public ContinuousOrchestrationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ContinuousOrchestrationException(Throwable cause) {
        super(cause);
    }

    public ContinuousOrchestrationException(
        String message,
        Throwable cause,
        boolean enableSuppression,
        boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
