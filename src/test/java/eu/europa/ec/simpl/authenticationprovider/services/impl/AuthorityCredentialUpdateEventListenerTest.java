package eu.europa.ec.simpl.authenticationprovider.services.impl;

import static eu.europa.ec.simpl.common.test.TestUtil.a;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.simpl.api.identityprovider.v1.exchanges.MtlsApi;
import eu.europa.ec.simpl.authenticationprovider.event.OnCredentialUpdateEvent;
import eu.europa.ec.simpl.authenticationprovider.services.CredentialService;
import eu.europa.ec.simpl.common.messaging.kafka.outbox.MessagePublisher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuthorityCredentialUpdateEventListenerTest {

    @Mock
    ObjectMapper objectMapper;

    @Mock
    MessagePublisher messagePublisher;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    MtlsApi identityProviderTierOneMtlsApi;

    @Mock
    CredentialService credentialService;

    @InjectMocks
    AuthorityCredentialUpdateEventListener authorityCredentialUpdateEventListener;

    @Test
    void testHandleInvalidateCredentialWhenCredentialIsDeletedShouldNotGetParticipantId() {
        authorityCredentialUpdateEventListener.handleInvalidateCredential(new OnCredentialUpdateEvent(null));
        then(messagePublisher).should().publish(any(), any());
    }

    @Test
    void testHandleInvalidateCredentialWhenCreAbstractCredentialServiceicipantId() {
        authorityCredentialUpdateEventListener.handleInvalidateCredential(
                new OnCredentialUpdateEvent(a(AbstractCredentialService.Credential.class)));
        then(messagePublisher).should().publish(any(), any());
        then(credentialService).should().getCredentialId(any());
        then(identityProviderTierOneMtlsApi).should().whoami(any());
    }
}
