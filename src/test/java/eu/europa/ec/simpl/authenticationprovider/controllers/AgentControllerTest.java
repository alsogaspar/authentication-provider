package eu.europa.ec.simpl.authenticationprovider.controllers;

import static eu.europa.ec.simpl.common.test.TestUtil.a;
import static eu.europa.ec.simpl.common.test.TestUtil.anURI;
import static org.mockito.Mockito.verify;

import eu.europa.ec.simpl.authenticationprovider.services.AgentService;
import eu.europa.ec.simpl.authenticationprovider.services.CredentialId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AgentControllerTest {

    @Mock
    AgentService agentService;

    @InjectMocks
    AgentController agentController;

    @Test
    void echo() {
        agentController.echo();
        verify(agentService).echo();
    }

    @Test
    void getIdentityAttributesWithOwnership() {
        agentController.getIdentityAttributesWithOwnership();
        verify(agentService).getAndSyncIdentityAttributes();
    }

    @Test
    void getParticipantIdentityAttributes() {
        var credentialId = a(String.class);
        agentController.getParticipantIdentityAttributes(credentialId);
        verify(agentService).getParticipantIdentityAttributes(CredentialId.decode(credentialId));
    }

    @Test
    void ping() {
        String fqdn = anURI().getHost();
        agentController.ping(fqdn);
        verify(agentService).ping(fqdn);
    }
}
