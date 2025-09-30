package eu.europa.ec.simpl.authenticationprovider.exceptions;

import org.springframework.http.HttpStatus;

public class InvalidCredentialException extends CredentialException {

    public InvalidCredentialException() {
        super(HttpStatus.BAD_REQUEST, "Invalid Credential");
    }
}
