package eu.europa.ec.simpl.authenticationprovider.services.impl;

import static eu.europa.ec.simpl.common.test.TestUtil.a;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mockStatic;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.simpl.authenticationprovider.configurations.mtls.MtlsClientFactory;
import eu.europa.ec.simpl.authenticationprovider.configurations.mtls.MtlsClientProperties;
import eu.europa.ec.simpl.authenticationprovider.event.OnCredentialUpdateEvent;
import eu.europa.ec.simpl.authenticationprovider.services.KeyPairService;
import eu.europa.ec.simpl.common.messaging.kafka.outbox.MessagePublisher;
import eu.europa.ec.simpl.common.utils.CredentialUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ParticipantCredentialUpdateEventListenerTest {

    @Mock
    ObjectMapper objectMapper;

    @Mock
    MessagePublisher messagePublisher;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    KeyPairService keyPairService;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    MtlsClientFactory mtlsClientFactory;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    MtlsClientProperties mtlsClientProperties;

    @InjectMocks
    ParticipantCredentialUpdateEventListener participantCredentialUpdateEventListener;

    @Test
    void testHandleInvalidateCredentialWhenCredentialIsDeletedShouldNotGetParticipantId() {
        participantCredentialUpdateEventListener.handleInvalidateCredential(new OnCredentialUpdateEvent(null));
        then(messagePublisher).should().publish(any(), any());
    }

    @Test
    void testHandleInvalidateCredentialWhenCredentialIsDeletedShouldGetParticipantId() {
        try (var credentialUtil = mockStatic(CredentialUtil.class)) {
            participantCredentialUpdateEventListener.handleInvalidateCredential(
                    new OnCredentialUpdateEvent(a(AbstractCredentialService.Credential.class)));
            then(messagePublisher).should().publish(any(), any());
            then(keyPairService).should().getInstalledKeyPair();
            then(mtlsClientFactory).should().buildAuthorityClient(any(), any());
        }
    }
}
