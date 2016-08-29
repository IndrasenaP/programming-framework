package eu.smartsocietyproject.peermanager;

public class PeerManagerException extends Exception {
    public PeerManagerException() {
    }

    public PeerManagerException(String message) {
        super(message);
    }

    public PeerManagerException(String message, Throwable cause) {
        super(message, cause);
    }

    public PeerManagerException(Throwable cause) {
        super(cause);
    }

    public PeerManagerException(
        String message,
        Throwable cause,
        boolean enableSuppression,
        boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
