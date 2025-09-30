package eu.europa.ec.simpl.authenticationprovider.services.impl;

import eu.europa.ec.simpl.authenticationprovider.configurations.SecurityProperties;
import eu.europa.ec.simpl.authenticationprovider.mappers.CredentialServiceMapper;
import eu.europa.ec.simpl.authenticationprovider.repositories.CredentialVaultRepository;
import eu.europa.ec.simpl.authenticationprovider.services.KeyPairService;
import java.util.List;
import java.util.Optional;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@ConditionalOnProperty(
        name = SecurityProperties.completeSecretLocationProperty,
        havingValue = SecurityProperties.locationVault)
public class CredentialVaultService extends AbstractCredentialService {

    public final CredentialVaultRepository credentialVaultRepository;
    public final CredentialServiceMapper mapper;

    public CredentialVaultService(
            ApplicationEventPublisher publisher,
            KeyPairService keyPairService,
            @Nullable CredentialVaultRepository credentialVaultRepository,
            CredentialServiceMapper mapper) {
        super(publisher, keyPairService);
        this.credentialVaultRepository = Optional.ofNullable(credentialVaultRepository)
                .orElseThrow(() -> new IllegalStateException("Missing Vault configuration"));
        this.mapper = mapper;
    }

    @Override
    public Credential saveCredential(Credential credential) {
        var crd = mapper.toVault(credential);
        credentialVaultRepository.save(mapper.toVault(credential));
        return mapper.fromVault(crd);
    }

    @Override
    public boolean hasStoredCredential() {
        return credentialVaultRepository.hasOne();
    }

    @Override
    public List<Credential> findAllCredentials() {
        return credentialVaultRepository.findAll().stream()
                .map(mapper::fromVault)
                .toList();
    }

    @Override
    public void deleteStoredCredential() {
        credentialVaultRepository.deleteAll();
    }
}
