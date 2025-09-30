package eu.europa.ec.simpl.authenticationprovider.configurations;

import eu.europa.ec.simpl.api.identityprovider.v1.exchanges.MtlsApi;
import eu.europa.ec.simpl.common.annotations.Authority;
import eu.europa.ec.simpl.common.exchanges.usersroles.CredentialExchange;
import eu.europa.ec.simpl.common.messageconverters.StreamingResponseBodyMessageConverter;
import java.net.URI;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Log4j2
@Configuration
public class ClientConfig {

    private static final String V1_PREFIX = "v1";
    private final MicroserviceProperties properties;

    public ClientConfig(MicroserviceProperties properties) {
        this.properties = properties;
    }

    @Bean
    @Authority
    public MtlsApi tierOnePublicKeyApi(RestClient.Builder restClientBuilder) {
        return buildExchange(properties.identityProvider().url().resolve(V1_PREFIX), restClientBuilder, MtlsApi.class);
    }

    @Bean
    public CredentialExchange credentialExchange(RestClient.Builder restClientBuilder) {
        return buildExchange(
                properties.usersRoles().url(),
                restClientBuilder.messageConverters(l -> l.add(new StreamingResponseBodyMessageConverter())),
                CredentialExchange.class);
    }

    private static <E> E buildExchange(URI baseurl, RestClient.Builder restClientBuilder, Class<E> clazz) {
        var restClient = restClientBuilder.baseUrl(baseurl).build();
        var adapter = RestClientAdapter.create(restClient);
        var factory = HttpServiceProxyFactory.builderFor(adapter).build();
        return factory.createClient(clazz);
    }
}
