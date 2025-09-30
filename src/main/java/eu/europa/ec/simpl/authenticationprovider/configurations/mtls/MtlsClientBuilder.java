package eu.europa.ec.simpl.authenticationprovider.configurations.mtls;

import eu.europa.ec.simpl.common.exchanges.mtls.AuthorityExchange;
import eu.europa.ec.simpl.common.exchanges.mtls.ParticipantExchange;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class MtlsClientBuilder {
    private final MtlsClientFactory mtlsClientFactory;
    private final MtlsClientProperties mtlsClientProperties;

    public MtlsClientBuilder(MtlsClientFactory mtlsClientFactory, MtlsClientProperties mtlsClientProperties) {
        this.mtlsClientFactory = mtlsClientFactory;
        this.mtlsClientProperties = mtlsClientProperties;
    }

    public AuthorityExchange buildAuthorityClient() {
        return mtlsClientFactory.buildAuthorityClient(
                mtlsClientProperties.authority().url(), null);
    }

    public ParticipantExchange buildParticipantClient(String url) {
        return mtlsClientFactory.buildParticipantClient(url, null);
    }
}
