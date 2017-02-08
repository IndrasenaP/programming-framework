package eu.smartsocietyproject.incentiveserver;


public class IncentiveServerException extends Exception {
    public IncentiveServerException() {
    }

    public IncentiveServerException(String message) {
        super(message);
    }

    public IncentiveServerException(String message, Throwable cause) {
        super(message, cause);
    }

    public IncentiveServerException(Throwable cause) {
        super(cause);
    }

    public IncentiveServerException(
            String message,
            Throwable cause,
            boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
