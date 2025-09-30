package eu.europa.ec.simpl.authenticationprovider.services.impl;

import eu.europa.ec.simpl.authenticationprovider.configurations.SecurityProperties;
import eu.europa.ec.simpl.authenticationprovider.mappers.CredentialServiceMapper;
import eu.europa.ec.simpl.authenticationprovider.repositories.CredentialRepository;
import eu.europa.ec.simpl.authenticationprovider.services.KeyPairService;
import java.util.List;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(
        name = SecurityProperties.completeSecretLocationProperty,
        havingValue = SecurityProperties.locationDatabase)
public class CredentialDatabaseService extends AbstractCredentialService {

    private final CredentialRepository repository;
    private final CredentialServiceMapper mapper;

    public CredentialDatabaseService(
            CredentialRepository repository,
            CredentialServiceMapper mapper,
            ApplicationEventPublisher publisher,
            KeyPairService keyPairService) {
        super(publisher, keyPairService);
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Credential saveCredential(Credential credential) {
        var entity = mapper.toEntity(credential);
        repository.save(entity);
        return mapper.fromEntity(entity);
    }

    @Override
    public boolean hasStoredCredential() {
        return repository.count() > 0;
    }

    @Override
    public List<Credential> findAllCredentials() {
        return repository.findAll().stream().map(mapper::fromEntity).toList();
    }

    @Override
    public void deleteStoredCredential() {
        repository.deleteAll();
    }
}
