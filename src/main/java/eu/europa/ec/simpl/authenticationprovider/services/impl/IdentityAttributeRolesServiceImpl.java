package eu.europa.ec.simpl.authenticationprovider.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.simpl.authenticationprovider.services.IdentityAttributeRolesService;
import eu.europa.ec.simpl.common.constants.Topics;
import eu.europa.ec.simpl.common.messaging.kafka.outbox.MessagePublisher;
import eu.europa.ec.simpl.events.authenticationprovider.v1.AssignedIdentityAttributesUpdatedEvent;
import java.util.List;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class IdentityAttributeRolesServiceImpl implements IdentityAttributeRolesService {

    private final MessagePublisher messagePublisher;
    private final ObjectMapper objectMapper;
    private final String topicPrefix;

    public IdentityAttributeRolesServiceImpl(
            MessagePublisher messagePublisher,
            ObjectMapper objectMapper,
            @Value("${simpl.kafka.topic.prefix}") String topicPrefix) {
        this.messagePublisher = messagePublisher;
        this.objectMapper = objectMapper;
        this.topicPrefix = topicPrefix;
    }

    @Override
    public void updateAssignments(List<String> idaCodes) {
        var message = toJson(idaCodes);
        var topic = "%s%s".formatted(topicPrefix, Topics.AUTHENTICATION_PROVIDER_IDENTITY_ATTRIBUTES_CHANGED_EVENT);
        messagePublisher.publish(topic, message);
    }

    @SneakyThrows
    private String toJson(List<String> idaCodes) {
        var event = new AssignedIdentityAttributesUpdatedEvent();
        event.setAssignedIdentityAttributesCodes(idaCodes);
        return objectMapper.writeValueAsString(event);
    }
}
