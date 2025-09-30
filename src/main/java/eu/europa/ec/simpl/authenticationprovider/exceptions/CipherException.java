package eu.europa.ec.simpl.authenticationprovider.exceptions;

public class CipherException extends RuntimeException {
    public CipherException(String message) {
        this(message, null);
    }

    public CipherException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
