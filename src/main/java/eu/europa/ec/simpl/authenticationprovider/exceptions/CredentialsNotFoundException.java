package eu.europa.ec.simpl.authenticationprovider.exceptions;

import eu.europa.ec.simpl.common.exceptions.StatusException;
import org.springframework.http.HttpStatus;

public class CredentialsNotFoundException extends StatusException {

    public CredentialsNotFoundException() {
        this(HttpStatus.NOT_FOUND, "Credentials not found");
    }

    public CredentialsNotFoundException(HttpStatus status, String message) {
        super(status, message);
    }
}
