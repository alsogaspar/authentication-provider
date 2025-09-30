package eu.europa.ec.simpl.authenticationprovider.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.simpl.authenticationprovider.configurations.mtls.MtlsClientFactory;
import eu.europa.ec.simpl.authenticationprovider.configurations.mtls.MtlsClientProperties;
import eu.europa.ec.simpl.authenticationprovider.services.KeyPairService;
import eu.europa.ec.simpl.common.annotations.Participant;
import eu.europa.ec.simpl.common.messaging.kafka.outbox.MessagePublisher;
import eu.europa.ec.simpl.common.utils.CredentialUtil;
import java.io.ByteArrayInputStream;
import java.security.PrivateKey;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Participant
public class ParticipantCredentialUpdateEventListener extends AbstractCredentialUpdateEventListener {

    private final KeyPairService keyPairService;
    private final MtlsClientFactory mtlsClientFactory;
    private final MtlsClientProperties mtlsClientProperties;

    protected ParticipantCredentialUpdateEventListener(
            ObjectMapper objectMapper,
            MessagePublisher messagePublisher,
            @Value("${simpl.kafka.topic.prefix}") String topicPrefix,
            KeyPairService keyPairService,
            MtlsClientFactory mtlsClientFactory,
            MtlsClientProperties mtlsClientProperties) {
        super(objectMapper, messagePublisher, topicPrefix);
        this.keyPairService = keyPairService;
        this.mtlsClientFactory = mtlsClientFactory;
        this.mtlsClientProperties = mtlsClientProperties;
    }

    public PrivateKey getPrivateKey() {
        var privateKey = keyPairService.getInstalledKeyPair().getPrivateKey();
        return CredentialUtil.loadPrivateKey(privateKey, "EC");
    }

    @Override
    protected UUID getParticipantId(byte[] content) {
        var keyStore = CredentialUtil.loadCredential(new ByteArrayInputStream(content), getPrivateKey());
        var mtlsClient = mtlsClientFactory.buildAuthorityClient(
                mtlsClientProperties.authority().url(), keyStore);
        return mtlsClient.whoami().getId();
    }
}
