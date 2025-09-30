package eu.europa.ec.simpl.authenticationprovider.services.impl;

import eu.europa.ec.simpl.authenticationprovider.configurations.SecurityProperties;
import eu.europa.ec.simpl.authenticationprovider.entities.ApplicantKeyPair;
import eu.europa.ec.simpl.authenticationprovider.mappers.KeyPairServiceMapper;
import eu.europa.ec.simpl.authenticationprovider.repositories.KeyPairRepository;
import eu.europa.ec.simpl.authenticationprovider.repositories.KeyPairVaultRepository;
import eu.europa.ec.simpl.authenticationprovider.services.CryptoService;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

@Service
public class KeyPairDatabaseVaultMigrationServiceImpl
        extends AbstractMigratorInitializer<
                KeyPairVaultRepository, KeyPairRepository, ApplicantKeyPair, KeyPairVaultRepository.ApplicantKeyPair> {

    private final KeyPairServiceMapper mapper;
    private final CryptoService cryptoService;

    public KeyPairDatabaseVaultMigrationServiceImpl(
            KeyPairRepository jpaRepository,
            @Nullable KeyPairVaultRepository vaultRepository,
            SecurityProperties securityProperties,
            KeyPairServiceMapper mapper,
            CryptoService cryptoService) {
        super(jpaRepository, vaultRepository, securityProperties);
        this.mapper = mapper;
        this.cryptoService = cryptoService;
    }

    @Override
    public String getName() {
        return "key pair";
    }

    @Override
    public ApplicantKeyPair fromVaultToEntity(KeyPairVaultRepository.ApplicantKeyPair vault) {
        return mapper.fromVaultToEntity(vault, cryptoService);
    }

    @Override
    public KeyPairVaultRepository.ApplicantKeyPair fromEntityToVault(ApplicantKeyPair entity) {
        return mapper.fromEntityToVault(entity, cryptoService);
    }

    @Override
    public Object getVaultId(KeyPairVaultRepository.ApplicantKeyPair vault) {
        return vault.getId();
    }

    @Override
    public Object getEntityId(ApplicantKeyPair entity) {
        return entity.getId();
    }

    @Override
    public void deleteEntityId(ApplicantKeyPair entity) {
        entity.setId(null);
    }
}
