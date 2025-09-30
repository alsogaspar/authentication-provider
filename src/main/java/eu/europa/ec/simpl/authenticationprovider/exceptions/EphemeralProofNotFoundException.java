package eu.europa.ec.simpl.authenticationprovider.exceptions;

import eu.europa.ec.simpl.common.exceptions.StatusException;
import org.springframework.http.HttpStatus;

public class EphemeralProofNotFoundException extends StatusException {

    public EphemeralProofNotFoundException() {
        this("Ephemeral Proof Not Found");
    }

    protected EphemeralProofNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}
