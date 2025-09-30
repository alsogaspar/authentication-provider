package eu.europa.ec.simpl.authenticationprovider.services.impl;

import static eu.europa.ec.simpl.common.test.TestUtil.a;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import eu.europa.ec.simpl.authenticationprovider.mappers.CredentialServiceMapperImpl;
import eu.europa.ec.simpl.authenticationprovider.repositories.CredentialVaultRepository;
import eu.europa.ec.simpl.authenticationprovider.services.KeyPairService;
import eu.europa.ec.simpl.authenticationprovider.utils.DtoUtils;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@Import({
    CredentialVaultService.class,
    CredentialServiceMapperImpl.class,
})
@TestPropertySource(
        properties = {
            "security.secret.location=vault",
        })
public class CredentialVaultServiceTest {

    @MockitoBean
    private ApplicationEventPublisher publisher;

    @MockitoBean
    private KeyPairService keyPairService;

    @MockitoBean
    private CredentialVaultRepository credentialVaultRepository;

    @Autowired
    private CredentialVaultService service;

    @Test
    public void saveCredentialTest() {
        var credential = a(AbstractCredentialService.Credential.class);
        var result = service.saveCredential(credential);
        assertThat(DtoUtils.areJsonEquals(credential, result));
        verify(credentialVaultRepository).save(argThat(cv -> DtoUtils.areJsonEquals(cv, credential)));
    }

    @Test
    public void hasStoredCredentialTest() {
        given(credentialVaultRepository.hasOne()).willReturn(true);
        var result = service.hasStoredCredential();
        assertThat(result).isTrue();
    }

    @Test
    public void findAllCredentialsTest() {
        var credential = a(CredentialVaultRepository.Credential.class);
        var credentials = List.of(credential);
        given(credentialVaultRepository.findAll()).willReturn(credentials);
        var result = service.findAllCredentials();
        assertThat(DtoUtils.areJsonEquals(credentials, result));
    }

    @Test
    public void deleteStoredCredentialTest() {
        service.deleteStoredCredential();
        verify(credentialVaultRepository).deleteAll();
    }
}
