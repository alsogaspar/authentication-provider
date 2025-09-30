package eu.europa.ec.simpl.authenticationprovider.services.impl;

import static eu.europa.ec.simpl.common.test.TestUtil.a;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import eu.europa.ec.simpl.authenticationprovider.entities.Credential;
import eu.europa.ec.simpl.authenticationprovider.mappers.CredentialServiceMapperImpl;
import eu.europa.ec.simpl.authenticationprovider.repositories.CredentialRepository;
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
    CredentialDatabaseService.class,
    CredentialServiceMapperImpl.class,
})
@TestPropertySource(properties = {"security.secret.location=database"})
public class CredentialDatabaseServiceTest {
    @MockitoBean
    private CredentialRepository repository;

    @MockitoBean
    private ApplicationEventPublisher publisher;

    @MockitoBean
    private KeyPairService keyPairService;

    @Autowired
    private CredentialDatabaseService service;

    @Test
    void saveCredentialTest() {
        var credential = a(AbstractCredentialService.Credential.class);
        var result = service.saveCredential(credential);
        assertThat(DtoUtils.areJsonEquals(credential, result)).isTrue();
        verify(repository).save(any());
    }

    @Test
    void hasStoredCredential_givenCount1_willReturnTrue() {
        given(repository.count()).willReturn(1L);
        var result = service.hasStoredCredential();
        assertThat(result).isTrue();
    }

    @Test
    void hasStoredCredential_givenCount0_willReturnFalse() {
        given(repository.count()).willReturn(0L);
        var result = service.hasStoredCredential();
        assertThat(result).isFalse();
    }

    @Test
    void findAllCredentialsTest() {
        var credential = a(Credential.class);
        var credentials = List.of(credential);
        given(repository.findAll()).willReturn(credentials);
        var result = service.findAllCredentials();
        assertThat(DtoUtils.areJsonEquals(result, credentials)).isTrue();
    }

    @Test
    void deleteStoredCredentialTest() {
        service.deleteStoredCredential();
        verify(repository).deleteAll();
    }
}
