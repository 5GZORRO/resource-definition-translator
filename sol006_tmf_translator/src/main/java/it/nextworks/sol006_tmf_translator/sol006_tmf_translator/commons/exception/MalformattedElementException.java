package it.nextworks.sol006_tmf_translator.sol006_tmf_translator.commons.exception;

public class MalformattedElementException extends Exception {

    public MalformattedElementException() {}

    public MalformattedElementException(String message) {
        super(message);
    }

    public MalformattedElementException(Throwable cause) {
        super(cause);
    }

    public MalformattedElementException(String message, Throwable cause) {
        super(message, cause);
    }

    public MalformattedElementException(String message, Throwable cause,
                                        boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}