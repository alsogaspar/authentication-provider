package eu.europa.ec.simpl.authenticationprovider.exceptions;

import eu.europa.ec.simpl.common.exceptions.StatusException;
import org.springframework.http.HttpStatus;

public class InvalidTierOneSessionException extends StatusException {

    public InvalidTierOneSessionException(String message) {
        super(HttpStatus.UNPROCESSABLE_ENTITY, message);
    }
}
