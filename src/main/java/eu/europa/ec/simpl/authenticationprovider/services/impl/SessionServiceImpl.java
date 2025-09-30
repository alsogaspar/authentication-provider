package eu.europa.ec.simpl.authenticationprovider.services.impl;

import eu.europa.ec.simpl.authenticationprovider.repositories.EphemeralProofRepository;
import eu.europa.ec.simpl.authenticationprovider.services.CredentialId;
import eu.europa.ec.simpl.authenticationprovider.services.SessionService;
import eu.europa.ec.simpl.common.ephemeralproof.JwtEphemeralProofParser;
import eu.europa.ec.simpl.common.model.dto.authenticationprovider.TierOneSessionDTO;
import eu.europa.ec.simpl.common.model.dto.securityattributesprovider.IdentityAttributeDTO;
import eu.europa.ec.simpl.common.utils.JwtUtil;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SessionServiceImpl implements SessionService {

    private final EphemeralProofRepository ephemeralProofRepository;
    private final Function<String, JwtEphemeralProofParser> ephemeralProofParserFactory;
    private final TierOneSessionValidator tierOneSessionValidator;

    public SessionServiceImpl(
            EphemeralProofRepository ephemeralProofRepository,
            @Autowired(required = false) Function<String, JwtEphemeralProofParser> ephemeralProofParserFactory,
            TierOneSessionValidator tierOneSessionValidator) {
        this.ephemeralProofRepository = ephemeralProofRepository;
        this.ephemeralProofParserFactory =
                Objects.requireNonNullElse(ephemeralProofParserFactory, JwtEphemeralProofParser::new);
        this.tierOneSessionValidator = tierOneSessionValidator;
    }

    @Override
    public List<IdentityAttributeDTO> getIdentityAttributesOfParticipant(CredentialId credentialId) {
        return getIdentityAttributesOfParticipant(credentialId.getContent());
    }

    @Override
    public List<IdentityAttributeDTO> getIdentityAttributesOfParticipant(String credentialId) {
        var ephemeralProof = ephemeralProofRepository.findByIdOrThrow(credentialId);
        return ephemeralProofParserFactory.apply(ephemeralProof.getContent()).getIdentityAttributes();
    }

    @Override
    @SneakyThrows
    public void validateTierOneSession(TierOneSessionDTO session) {
        var jwt = JwtUtil.parseJwt(session.getJwt());
        var credentialId =
                jwt.getJWTClaimsSet().getStringListClaim("public_keys").getFirst();
        tierOneSessionValidator.validate(jwt, credentialId);
    }
}
