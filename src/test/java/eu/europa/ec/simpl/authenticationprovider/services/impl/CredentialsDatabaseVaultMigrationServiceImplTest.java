package eu.europa.ec.simpl.authenticationprovider.services.impl;

import static eu.europa.ec.simpl.common.test.TestUtil.a;
import static org.assertj.core.api.Assertions.assertThat;

import eu.europa.ec.simpl.authenticationprovider.configurations.SecurityProperties;
import eu.europa.ec.simpl.authenticationprovider.entities.Credential;
import eu.europa.ec.simpl.authenticationprovider.mappers.CredentialServiceMapperImpl;
import eu.europa.ec.simpl.authenticationprovider.repositories.CredentialRepository;
import eu.europa.ec.simpl.authenticationprovider.repositories.CredentialVaultRepository;
import eu.europa.ec.simpl.authenticationprovider.utils.DtoUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@Import({
    CredentialsDatabaseVaultMigrationServiceImpl.class,
    CredentialServiceMapperImpl.class,
})
public class CredentialsDatabaseVaultMigrationServiceImplTest {
    @MockitoBean
    private CredentialRepository jpaRepository;

    @MockitoBean
    private CredentialVaultRepository vaultRepository;

    @MockitoBean
    private SecurityProperties securityProperties;

    @Autowired
    CredentialsDatabaseVaultMigrationServiceImpl service;

    @Test
    void getName_expectedNotNull() {
        var name = service.getName();
        assertThat(name).isNotNull();
    }

    @Test
    void fromVaultToEntityTest() {
        var credential = a(CredentialVaultRepository.Credential.class);
        var result = service.fromVaultToEntity(credential);
        assertThat(DtoUtils.areJsonEquals(credential, result)).isTrue();
    }

    @Test
    void fromEntityToVaultTest() {
        var credential = a(Credential.class);
        var result = service.fromEntityToVault(credential);
        assertThat(DtoUtils.areJsonEquals(credential, result)).isTrue();
    }

    @Test
    void getVaultIdTest() {
        var vault = a(CredentialVaultRepository.Credential.class);
        var result = service.getVaultId(vault);
        assertThat(result).isEqualTo(vault.getId());
    }

    @Test
    void getEntityIdTest() {
        var vault = a(Credential.class);
        var result = service.getEntityId(vault);
        assertThat(result).isEqualTo(vault.getId());
    }

    @Test
    void deleteEntityIdTest() {
        var vault = a(Credential.class);
        service.deleteEntityId(vault);
        assertThat(vault.getId()).isEqualTo(0L);
    }
}
