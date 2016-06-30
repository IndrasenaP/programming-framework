package eu.smartsocietyproject.pf.cbthandlers;

public class CBTLifecycleException extends Exception {
    public CBTLifecycleException() {
    }

    public CBTLifecycleException(String message) {
        super(message);
    }

    public CBTLifecycleException(String message, Throwable cause) {
        super(message, cause);
    }

    public CBTLifecycleException(Throwable cause) {
        super(cause);
    }

    public CBTLifecycleException(
            String message,
            Throwable cause,
            boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
