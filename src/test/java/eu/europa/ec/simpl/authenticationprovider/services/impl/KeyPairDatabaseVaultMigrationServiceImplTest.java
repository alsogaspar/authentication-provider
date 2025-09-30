package eu.europa.ec.simpl.authenticationprovider.services.impl;

import static eu.europa.ec.simpl.common.test.TestUtil.a;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

import eu.europa.ec.simpl.authenticationprovider.configurations.SecurityProperties;
import eu.europa.ec.simpl.authenticationprovider.entities.ApplicantKeyPair;
import eu.europa.ec.simpl.authenticationprovider.mappers.KeyPairServiceMapperImpl;
import eu.europa.ec.simpl.authenticationprovider.repositories.KeyPairRepository;
import eu.europa.ec.simpl.authenticationprovider.repositories.KeyPairVaultRepository;
import eu.europa.ec.simpl.authenticationprovider.services.CryptoService;
import eu.europa.ec.simpl.authenticationprovider.utils.DtoUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@Import({
    KeyPairServiceMapperImpl.class,
    KeyPairDatabaseVaultMigrationServiceImpl.class,
})
public class KeyPairDatabaseVaultMigrationServiceImplTest {
    @MockitoBean
    private KeyPairRepository jpaRepository;

    @MockitoBean
    private KeyPairVaultRepository vaultRepository;

    @MockitoBean
    private SecurityProperties securityProperties;

    @MockitoBean
    private CryptoService cryptoService;

    @Autowired
    private KeyPairDatabaseVaultMigrationServiceImpl service;

    @Test
    void getName_expectedNotNull() {
        var name = service.getName();
        assertThat(name).isNotNull();
    }

    @Test
    void fromVaultToEntityTest() {
        var keyPair = a(KeyPairVaultRepository.ApplicantKeyPair.class);
        given(cryptoService.encrypt(eq(keyPair.getPrivateKey()))).willReturn(keyPair.getPrivateKey());
        given(cryptoService.encrypt(eq(keyPair.getPublicKey()))).willReturn(keyPair.getPublicKey());
        var result = service.fromVaultToEntity(keyPair);
        assertThat(DtoUtils.areJsonEquals(keyPair, result)).isTrue();
    }

    @Test
    void fromEntityToVaultTest() {
        var keyPair = a(ApplicantKeyPair.class);
        given(cryptoService.decrypt(eq(keyPair.getPrivateKey()))).willReturn(keyPair.getPrivateKey());
        given(cryptoService.decrypt(eq(keyPair.getPublicKey()))).willReturn(keyPair.getPublicKey());
        var result = service.fromEntityToVault(keyPair);
        assertThat(DtoUtils.areJsonEquals(keyPair, result)).isTrue();
    }

    @Test
    void getVaultIdTest() {
        var vault = a(KeyPairVaultRepository.ApplicantKeyPair.class);
        var result = service.getVaultId(vault);
        assertThat(result).isEqualTo(vault.getId());
    }

    @Test
    void getEntityIdTest() {
        var vault = a(ApplicantKeyPair.class);
        var result = service.getEntityId(vault);
        assertThat(result).isEqualTo(vault.getId());
    }

    @Test
    void deleteEntityIdTest() {
        var vault = a(ApplicantKeyPair.class);
        service.deleteEntityId(vault);
        assertThat(vault.getId()).isNull();
    }
}
