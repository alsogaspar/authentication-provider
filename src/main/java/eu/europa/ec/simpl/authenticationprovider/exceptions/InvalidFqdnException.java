package eu.europa.ec.simpl.authenticationprovider.exceptions;

import eu.europa.ec.simpl.common.exceptions.StatusException;
import org.springframework.http.HttpStatus;

public class InvalidFqdnException extends StatusException {
    public InvalidFqdnException(String fqdn) {
        super(HttpStatus.BAD_REQUEST, "Invalid FQDN " + fqdn);
    }
}
