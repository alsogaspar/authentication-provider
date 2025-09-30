package eu.europa.ec.simpl.authenticationprovider.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.simpl.authenticationprovider.event.OnCredentialUpdateEvent;
import eu.europa.ec.simpl.common.constants.Topics;
import eu.europa.ec.simpl.common.messaging.kafka.outbox.MessagePublisher;
import eu.europa.ec.simpl.events.authenticationprovider.v1.CredentialUpdatedEvent;
import java.util.UUID;
import lombok.SneakyThrows;
import org.springframework.context.event.EventListener;

public abstract class AbstractCredentialUpdateEventListener {

    private final ObjectMapper objectMapper;
    private final MessagePublisher messagePublisher;
    private final String topicPrefix;

    protected AbstractCredentialUpdateEventListener(
            ObjectMapper objectMapper, MessagePublisher messagePublisher, String topicPrefix) {
        this.objectMapper = objectMapper;
        this.messagePublisher = messagePublisher;
        this.topicPrefix = topicPrefix;
    }

    protected abstract UUID getParticipantId(byte[] content);

    @EventListener(OnCredentialUpdateEvent.class)
    public void handleInvalidateCredential(OnCredentialUpdateEvent event) {
        boolean deleted = event.getNewCredential() == null;
        if (!deleted) {
            var content = event.getNewCredential().getContent();
            event.getNewCredential().setParticipantId(getParticipantId(content));
        }
        publishCredentialUpdatedEvent(deleted);
    }

    private void publishCredentialUpdatedEvent(boolean deleted) {
        var topic = "%s%s".formatted(topicPrefix, Topics.AUTHENTICATION_PROVIDER_CREDENTIAL_UPDATED_EVENT);
        var message = new CredentialUpdatedEvent();
        message.setDeleted(deleted);
        messagePublisher.publish(topic, toJson(message));
    }

    @SneakyThrows
    private String toJson(CredentialUpdatedEvent message) {
        return objectMapper.writeValueAsString(message);
    }
}
