package eu.smartsocietyproject.pf.orchestration;

public class NegotiationException extends Exception {
    public NegotiationException() {
    }

    public NegotiationException(String message) {
        super(message);
    }

    public NegotiationException(String message, Throwable cause) {
        super(message, cause);
    }

    public NegotiationException(Throwable cause) {
        super(cause);
    }

    public NegotiationException(
        String message,
        Throwable cause,
        boolean enableSuppression,
        boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
