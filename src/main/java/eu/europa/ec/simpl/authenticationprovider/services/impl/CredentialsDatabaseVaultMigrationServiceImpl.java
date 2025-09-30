package eu.europa.ec.simpl.authenticationprovider.services.impl;

import eu.europa.ec.simpl.authenticationprovider.configurations.SecurityProperties;
import eu.europa.ec.simpl.authenticationprovider.entities.Credential;
import eu.europa.ec.simpl.authenticationprovider.mappers.CredentialServiceMapper;
import eu.europa.ec.simpl.authenticationprovider.repositories.CredentialRepository;
import eu.europa.ec.simpl.authenticationprovider.repositories.CredentialVaultRepository;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

@Service
public class CredentialsDatabaseVaultMigrationServiceImpl
        extends AbstractMigratorInitializer<
                CredentialVaultRepository, CredentialRepository, Credential, CredentialVaultRepository.Credential> {

    private final CredentialServiceMapper mapper;

    public CredentialsDatabaseVaultMigrationServiceImpl(
            CredentialRepository jpaRepository,
            @Nullable CredentialVaultRepository vaultRepository,
            SecurityProperties securityProperties,
            CredentialServiceMapper mapper) {
        super(jpaRepository, vaultRepository, securityProperties);
        this.mapper = mapper;
    }

    @Override
    public String getName() {
        return "credentials";
    }

    @Override
    public Credential fromVaultToEntity(CredentialVaultRepository.Credential vault) {
        return mapper.fromVaultToEntity(vault);
    }

    @Override
    public CredentialVaultRepository.Credential fromEntityToVault(Credential entity) {
        return mapper.fromEntityToVault(entity);
    }

    @Override
    public Object getVaultId(CredentialVaultRepository.Credential vault) {
        return vault.getId();
    }

    @Override
    public Object getEntityId(Credential entity) {
        return entity.getId();
    }

    @Override
    public void deleteEntityId(Credential entity) {
        entity.setId(0L);
    }
}
