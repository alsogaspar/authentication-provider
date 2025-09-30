package eu.europa.ec.simpl.authenticationprovider.controllers;

import static eu.europa.ec.simpl.common.test.TestUtil.a;
import static org.mockito.Mockito.verify;

import eu.europa.ec.simpl.authenticationprovider.services.CredentialId;
import eu.europa.ec.simpl.authenticationprovider.services.SessionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SessionControllerTest {

    @Mock
    SessionService sessionService;

    @InjectMocks
    SessionController sessionController;

    @Test
    void getIdentityAttributesOfParticipant() {
        var credentialId = a(String.class);
        sessionController.getIdentityAttributesOfParticipant(credentialId);
        verify(sessionService).getIdentityAttributesOfParticipant(CredentialId.decode(credentialId));
    }
}
