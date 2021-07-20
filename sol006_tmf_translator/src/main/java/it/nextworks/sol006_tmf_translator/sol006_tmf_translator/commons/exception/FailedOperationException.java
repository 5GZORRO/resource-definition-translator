package it.nextworks.sol006_tmf_translator.sol006_tmf_translator.commons.exception;

public class FailedOperationException extends Exception {

    public FailedOperationException() {}

    public FailedOperationException(String message) {
        super(message);
    }

    public FailedOperationException(Throwable cause) {
        super(cause);
    }

    public FailedOperationException(String message, Throwable cause) {
        super(message, cause);
    }

    public FailedOperationException(String message, Throwable cause,
                                    boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}