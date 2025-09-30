package eu.europa.ec.simpl.authenticationprovider.controllers;

import static eu.europa.ec.simpl.common.test.TestUtil.a;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import eu.europa.ec.simpl.authenticationprovider.services.CredentialService;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

@ExtendWith(MockitoExtension.class)
class CredentialControllerTest {

    @Mock
    CredentialService credentialService;

    @InjectMocks
    CredentialController credentialController;

    @Test
    void uploadCredential() throws IOException {
        var mockMultipartFile = a(MockMultipartFile.class);
        credentialController.uploadCredential(mockMultipartFile);
        verify(credentialService).insert(mockMultipartFile.getBytes());
    }

    @Test
    void hasCredential() {
        credentialController.hasCredential();
        verify(credentialService).hasCredential();
    }

    @Test
    void delete() {
        credentialController.delete();
        verify(credentialService).deleteCredential();
    }

    @Test
    void downloadInstalledCredentials() throws IOException {
        when(credentialService.getCredential()).thenReturn(new byte[0]);
        credentialController.downloadInstalledCredentials();
        verify(credentialService).getCredential();
    }

    @Test
    void getPublicKey() {
        credentialController.getPublicKey();
        verify(credentialService).getPublicKey();
    }

    @Test
    void getMyParticipantId() {
        credentialController.getMyParticipantId();
        verify(credentialService).getMyParticipantId();
    }
}
