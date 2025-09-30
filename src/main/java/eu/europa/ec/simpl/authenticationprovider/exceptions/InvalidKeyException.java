package eu.europa.ec.simpl.authenticationprovider.exceptions;

import eu.europa.ec.simpl.common.exceptions.StatusException;
import org.springframework.http.HttpStatus;

public class InvalidKeyException extends StatusException {

    public InvalidKeyException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
