package eu.europa.ec.simpl.authenticationprovider.services.impl;

import eu.europa.ec.simpl.authenticationprovider.exceptions.CredentialException;
import eu.europa.ec.simpl.authenticationprovider.repositories.EphemeralProofRepository;
import eu.europa.ec.simpl.authenticationprovider.services.CredentialService;
import eu.europa.ec.simpl.authenticationprovider.services.IdentityAttributeService;
import eu.europa.ec.simpl.authenticationprovider.services.KeyPairService;
import eu.europa.ec.simpl.client.core.adapters.AuthenticationProviderAdapter;
import eu.europa.ec.simpl.common.ephemeralproof.JwtEphemeralProofParser;
import eu.europa.ec.simpl.common.redis.entity.EphemeralProof;
import eu.europa.ec.simpl.common.utils.CredentialUtil;
import java.io.ByteArrayInputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class EphemeralProofAdapterImpl implements AuthenticationProviderAdapter {

    private final EphemeralProofRepository ephemeralProofRepository;
    private final IdentityAttributeService identityAttributeService;
    private final KeyPairService keyPairService;
    private final CredentialService credentialService;
    private final Function<String, JwtEphemeralProofParser> ephemeralProofParserFactory;

    public EphemeralProofAdapterImpl(
            EphemeralProofRepository ephemeralProofRepository,
            IdentityAttributeService identityAttributeService,
            KeyPairService keyPairService,
            CredentialService credentialService,
            @Autowired(required = false) Function<String, JwtEphemeralProofParser> ephemeralProofParserFactory) {
        this.ephemeralProofRepository = ephemeralProofRepository;
        this.identityAttributeService = identityAttributeService;
        this.keyPairService = keyPairService;
        this.credentialService = credentialService;
        this.ephemeralProofParserFactory =
                Objects.requireNonNullElse(ephemeralProofParserFactory, JwtEphemeralProofParser::new);
    }

    @Override
    public KeyStore getKeyStore() {
        if (!credentialService.hasCredential()) {
            log.error("No credential found");
            throw new IllegalStateException("No credential found");
        }

        return CredentialUtil.loadCredential(
                new ByteArrayInputStream(credentialService.getCredential()), getPrivateKey());
    }

    private PrivateKey getPrivateKey() {
        var privateKey = keyPairService.getInstalledKeyPair().getPrivateKey();
        return CredentialUtil.loadPrivateKey(privateKey, "EC");
    }

    @Override
    public boolean validateCredential(String credential) {
        try {
            credentialService.validateCredential(credential);
            return true;
        } catch (CredentialException e) {
            return false;
        }
    }

    @Override
    public Optional<String> loadEphemeralProof() {
        log.info("Start loading Ephemeral proof from cache...");
        var credentialId = credentialService.getCredentialId();
        try {
            return ephemeralProofRepository.findById(credentialId).map(EphemeralProof::getContent);
        } catch (Exception e) {
            log.error("Unable to access redis to retrieve the ephemeral proof", e);
            return Optional.empty();
        }
    }

    @Override
    public void storeEphemeralProof(String ephemeralProof) {
        var credentialId = credentialService.getCredentialId();
        var parser = ephemeralProofParserFactory.apply(ephemeralProof);
        try {
            ephemeralProofRepository.save(new EphemeralProof()
                    .setCredentialId(credentialId)
                    .setContent(parser.getRaw())
                    .setExpiration(getTimeToLive(parser.getExpiration())));

            tryUpdateAssignedIdentityAttributes(parser);
        } catch (Exception e) {
            log.error(
                    "Unable to access redis to store the ephemeral proof (identity attributes will not be synchronized)",
                    e);
        }
    }

    private void tryUpdateAssignedIdentityAttributes(JwtEphemeralProofParser parser) {
        try {
            identityAttributeService.updateAssignedIdentityAttributes(parser.getIdentityAttributes());
        } catch (Exception e) {
            log.error("Unable to update assigned identity attributes", e);
        }
    }

    private static Duration getTimeToLive(Instant expiration) {
        return Duration.between(Instant.now(), expiration);
    }
}
