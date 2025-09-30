package eu.europa.ec.simpl.authenticationprovider.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.simpl.api.identityprovider.v1.exchanges.MtlsApi;
import eu.europa.ec.simpl.authenticationprovider.services.CredentialService;
import eu.europa.ec.simpl.common.annotations.Authority;
import eu.europa.ec.simpl.common.messaging.kafka.outbox.MessagePublisher;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Authority
public class AuthorityCredentialUpdateEventListener extends AbstractCredentialUpdateEventListener {

    private final MtlsApi identityProviderTierOneMtlsApi;
    private final CredentialService credentialService;

    protected AuthorityCredentialUpdateEventListener(
            ObjectMapper objectMapper,
            MessagePublisher messagePublisher,
            @Value("${simpl.kafka.topic.prefix}") String topicPrefix,
            MtlsApi identityProviderTierOneMtlsApi,
            CredentialService credentialService) {
        super(objectMapper, messagePublisher, topicPrefix);
        this.identityProviderTierOneMtlsApi = identityProviderTierOneMtlsApi;
        this.credentialService = credentialService;
    }

    @Override
    protected UUID getParticipantId(byte[] content) {
        var credentialId = credentialService.getCredentialId(content);
        return identityProviderTierOneMtlsApi.whoami(credentialId).getId();
    }
}
