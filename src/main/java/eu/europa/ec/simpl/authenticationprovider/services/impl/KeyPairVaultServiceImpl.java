package eu.europa.ec.simpl.authenticationprovider.services.impl;

import eu.europa.ec.simpl.authenticationprovider.configurations.KeyPairGenerationAlgorithm;
import eu.europa.ec.simpl.authenticationprovider.configurations.SecurityProperties;
import eu.europa.ec.simpl.authenticationprovider.mappers.KeyPairServiceMapper;
import eu.europa.ec.simpl.authenticationprovider.repositories.KeyPairVaultRepository;
import java.util.Optional;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(
        name = SecurityProperties.completeSecretLocationProperty,
        havingValue = SecurityProperties.locationVault)
public class KeyPairVaultServiceImpl extends AbstractKeyPairService {

    private final KeyPairVaultRepository repository;
    private final KeyPairServiceMapper mapper;

    public KeyPairVaultServiceImpl(
            KeyPairGenerationAlgorithm keyPairGenerationAlgorithm,
            ApplicationEventPublisher applicationEventPublisher,
            KeyPairVaultRepository repository,
            KeyPairServiceMapper mapper) {
        super(keyPairGenerationAlgorithm, applicationEventPublisher);
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    protected void save(ApplicantKeyPair applicantKeyPair) {
        repository.save(mapper.toVault(applicantKeyPair));
    }

    @Override
    protected Optional<ApplicantKeyPair> getKeyPair() {
        return repository.getKeyPair().map(mapper::fromVault);
    }
}
