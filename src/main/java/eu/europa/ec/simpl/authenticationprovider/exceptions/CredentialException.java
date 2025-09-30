package eu.europa.ec.simpl.authenticationprovider.exceptions;

import eu.europa.ec.simpl.common.exceptions.StatusException;
import org.jetbrains.annotations.Nullable;
import org.springframework.http.HttpStatus;

public abstract class CredentialException extends StatusException {
    protected CredentialException(@Nullable HttpStatus status, String message) {
        super(status, message);
    }
}
