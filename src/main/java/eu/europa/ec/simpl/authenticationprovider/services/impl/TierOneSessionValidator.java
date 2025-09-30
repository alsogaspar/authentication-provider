package eu.europa.ec.simpl.authenticationprovider.services.impl;

import com.nimbusds.jwt.SignedJWT;
import eu.europa.ec.simpl.authenticationprovider.exceptions.InvalidTierOneSessionException;
import eu.europa.ec.simpl.authenticationprovider.repositories.EphemeralProofRepository;
import eu.europa.ec.simpl.common.ephemeralproof.JwtEphemeralProofParser;
import eu.europa.ec.simpl.common.exceptions.StatusException;
import eu.europa.ec.simpl.common.redis.entity.EphemeralProof;
import eu.europa.ec.simpl.common.services.AbstractTierOneSessionValidator;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class TierOneSessionValidator extends AbstractTierOneSessionValidator<JwtEphemeralProofParser> {

    private final EphemeralProofRepository repository;

    @Override
    public void validate(SignedJWT signedJWT, String credentialId) {
        try {
            super.validate(signedJWT, credentialId);
        } catch (StatusException e) {
            throw new InvalidTierOneSessionException(e.getMessage());
        }
    }

    public TierOneSessionValidator(EphemeralProofRepository repository) {
        this.repository = repository;
    }

    @Override
    protected Optional<EphemeralProof> fetchEphemeralProofById(String credentialId) {
        return repository.findById(credentialId);
    }

    @Override
    protected JwtEphemeralProofParser getEphemeralProofParser(String rawEphemeralProof) {
        return new JwtEphemeralProofParser(rawEphemeralProof);
    }
}
