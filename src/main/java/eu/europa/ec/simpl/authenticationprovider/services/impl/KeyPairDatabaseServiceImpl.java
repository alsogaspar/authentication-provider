package eu.europa.ec.simpl.authenticationprovider.services.impl;

import eu.europa.ec.simpl.authenticationprovider.configurations.KeyPairGenerationAlgorithm;
import eu.europa.ec.simpl.authenticationprovider.configurations.SecurityProperties;
import eu.europa.ec.simpl.authenticationprovider.mappers.KeyPairServiceMapper;
import eu.europa.ec.simpl.authenticationprovider.repositories.KeyPairRepository;
import eu.europa.ec.simpl.authenticationprovider.services.CryptoService;
import java.util.Optional;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(
        name = SecurityProperties.completeSecretLocationProperty,
        havingValue = SecurityProperties.locationDatabase)
public class KeyPairDatabaseServiceImpl extends AbstractKeyPairService {

    private final CryptoService cryptoService;
    private final KeyPairRepository keyPairRepository;
    private final KeyPairServiceMapper mapper;

    public KeyPairDatabaseServiceImpl(
            KeyPairGenerationAlgorithm keyPairGenerationAlgorithm,
            ApplicationEventPublisher applicationEventPublisher,
            CryptoService cryptoService,
            KeyPairRepository keyPairRepository,
            KeyPairServiceMapper mapper) {
        super(keyPairGenerationAlgorithm, applicationEventPublisher);
        this.cryptoService = cryptoService;
        this.keyPairRepository = keyPairRepository;
        this.mapper = mapper;
    }

    @Override
    protected void save(AbstractKeyPairService.ApplicantKeyPair applicantKeyPair) {
        var entity = mapper.toEntity(applicantKeyPair, cryptoService);
        keyPairRepository.save(entity);
        mapper.fillFromEntity(applicantKeyPair, entity, cryptoService);
    }

    @Override
    protected Optional<AbstractKeyPairService.ApplicantKeyPair> getKeyPair() {
        return keyPairRepository.findAllByOrderByCreationTimestampDesc().stream()
                .findFirst()
                .map(keyPair -> mapper.fromEntity(keyPair, cryptoService));
    }
}
