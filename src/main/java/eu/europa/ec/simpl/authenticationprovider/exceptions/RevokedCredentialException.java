package eu.europa.ec.simpl.authenticationprovider.exceptions;

import org.springframework.http.HttpStatus;

public class RevokedCredentialException extends CredentialException {
    public RevokedCredentialException() {
        super(HttpStatus.BAD_REQUEST, "Credential Revoked");
    }
}
