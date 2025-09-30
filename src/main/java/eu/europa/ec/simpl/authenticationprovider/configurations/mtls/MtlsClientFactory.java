package eu.europa.ec.simpl.authenticationprovider.configurations.mtls;

import eu.europa.ec.simpl.client.core.SimplClient;
import eu.europa.ec.simpl.client.core.adapters.AuthenticationProviderAdapter;
import eu.europa.ec.simpl.client.core.ssl.SslInfo;
import eu.europa.ec.simpl.client.okhttp.OkHttpSimplClient;
import eu.europa.ec.simpl.common.argumentresolvers.PageableArgumentResolver;
import eu.europa.ec.simpl.common.argumentresolvers.QueryParamsArgumentResolver;
import eu.europa.ec.simpl.common.exchanges.mtls.AuthorityExchange;
import eu.europa.ec.simpl.common.exchanges.mtls.ParticipantExchange;
import eu.europa.ec.simpl.common.interceptors.TierOneTokenPropagatorInterceptor;
import eu.europa.ec.simpl.common.utils.OkHttpClientHttpRequestFactory;
import java.security.KeyStore;
import lombok.extern.log4j.Log4j2;
import okhttp3.OkHttpClient;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Log4j2
@Component
public class MtlsClientFactory {

    private final OkHttpSimplClient okHttpSimplClient;
    private final TierOneTokenPropagatorInterceptor tokenPropagator;
    private final MtlsClientProperties clientProperties;
    private final AuthenticationProviderAdapter ephemeralProofAdapter;
    private final RestClient.Builder restClientBuilder;

    public MtlsClientFactory(
            OkHttpSimplClient okHttpSimplClient,
            TierOneTokenPropagatorInterceptor tokenPropagator,
            MtlsClientProperties clientProperties,
            AuthenticationProviderAdapter ephemeralProofAdapter,
            RestClient.Builder restClientBuilder) {
        this.okHttpSimplClient = okHttpSimplClient;
        this.tokenPropagator = tokenPropagator;
        this.clientProperties = clientProperties;
        this.ephemeralProofAdapter = ephemeralProofAdapter;
        this.restClientBuilder = restClientBuilder;
    }

    public AuthorityExchange buildAuthorityClient(String url, @Nullable KeyStore keyStore) {
        log.info("Creating MTLS Client to {}", url);
        var okHttpClientBuilder = buildClient(okHttpSimplClient.builder(), keyStore);
        return buildExchange(okHttpClientBuilder.build(), url, restClientBuilder, AuthorityExchange.class);
    }

    public ParticipantExchange buildParticipantClient(String url, @Nullable KeyStore keyStore) {
        return buildAuthorityClient(url, keyStore);
    }

    private <E> E buildExchange(
            OkHttpClient okHttpClient, String baseurl, RestClient.Builder restClientBuilder, Class<E> clazz) {
        var okHttp3ClientHttpRequestFactory = new OkHttpClientHttpRequestFactory(okHttpClient);
        var restClient = restClientBuilder
                .requestFactory(okHttp3ClientHttpRequestFactory)
                .baseUrl(baseurl)
                .build();
        var adapter = RestClientAdapter.create(restClient);
        var factory = HttpServiceProxyFactory.builderFor(adapter)
                .customArgumentResolver(new PageableArgumentResolver())
                .customArgumentResolver(new QueryParamsArgumentResolver())
                .build();
        return factory.createClient(clazz);
    }

    private <T> T buildClient(SimplClient.Builder<T> clientBuilder, @Nullable KeyStore keyStore) {

        if (keyStore != null) {
            clientBuilder.setSslInfoSupplier(() -> new SslInfo(keyStore));
        }

        return clientBuilder
                .setAuthorizationHeaderSupplier(tokenPropagator)
                .setAuthenticationProviderAdapter(ephemeralProofAdapter)
                .setAuthorityUrlSupplier(() -> clientProperties.authority().url())
                .build();
    }
}
