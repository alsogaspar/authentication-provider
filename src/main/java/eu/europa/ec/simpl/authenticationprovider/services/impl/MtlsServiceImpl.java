package eu.europa.ec.simpl.authenticationprovider.services.impl;

import eu.europa.ec.simpl.authenticationprovider.repositories.EphemeralProofRepository;
import eu.europa.ec.simpl.authenticationprovider.services.MtlsService;
import eu.europa.ec.simpl.authenticationprovider.services.SessionService;
import eu.europa.ec.simpl.common.ephemeralproof.JwtEphemeralProofParser;
import eu.europa.ec.simpl.common.exchanges.mtls.AuthorityExchange;
import eu.europa.ec.simpl.common.model.dto.identityprovider.ParticipantWithIdentityAttributesDTO;
import eu.europa.ec.simpl.common.redis.entity.EphemeralProof;
import java.time.Duration;
import java.time.Instant;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Log4j2
@Service
@Validated
public class MtlsServiceImpl implements MtlsService {

    private final SessionService sessionService;
    private final EphemeralProofRepository repository;

    public MtlsServiceImpl(
            SessionService sessionService, EphemeralProofRepository repository, AuthorityExchange authorityExchange) {
        this.sessionService = sessionService;
        this.repository = repository;
    }

    @Override
    public ParticipantWithIdentityAttributesDTO ping(String credentialId) {
        return new ParticipantWithIdentityAttributesDTO()
                .setIdentityAttributes(sessionService.getIdentityAttributesOfParticipant(credentialId));
    }

    @Override
    public void insertEphemeralProof(String credentialId, String ephemeralProof) {
        log.info("Caching ephemeral proof for participant {}", credentialId);
        repository.save(new EphemeralProof()
                .setCredentialId(credentialId)
                .setContent(ephemeralProof)
                .setExpiration(getTimeToLive(ephemeralProof)));
    }

    private Duration getTimeToLive(String ephemeralProof) {
        var expireTime = new JwtEphemeralProofParser(ephemeralProof)
                .getClaimsSet()
                .getExpirationTime()
                .toInstant();
        return Duration.between(Instant.now(), expireTime);
    }
}
