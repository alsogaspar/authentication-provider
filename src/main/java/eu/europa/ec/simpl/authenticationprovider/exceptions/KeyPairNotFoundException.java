package eu.europa.ec.simpl.authenticationprovider.exceptions;

import eu.europa.ec.simpl.common.exceptions.StatusException;
import org.springframework.http.HttpStatus;

public class KeyPairNotFoundException extends StatusException {

    public KeyPairNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }

    public KeyPairNotFoundException() {
        this("No KeyPair Found, can't generate CSR");
    }
}
