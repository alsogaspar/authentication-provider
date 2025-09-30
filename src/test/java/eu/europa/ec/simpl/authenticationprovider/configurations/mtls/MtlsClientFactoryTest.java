package eu.europa.ec.simpl.authenticationprovider.configurations.mtls;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mock;

import eu.europa.ec.simpl.client.core.adapters.AuthenticationProviderAdapter;
import eu.europa.ec.simpl.client.okhttp.OkHttpSimplClient;
import eu.europa.ec.simpl.common.interceptors.TierOneTokenPropagatorInterceptor;
import java.security.KeyStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClient;

@ExtendWith(MockitoExtension.class)
public class MtlsClientFactoryTest {
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private OkHttpSimplClient okHttpSimplClient;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private TierOneTokenPropagatorInterceptor tokenPropagator;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private MtlsClientProperties clientProperties;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private AuthenticationProviderAdapter ephemeralProofAdapter;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private RestClient.Builder restClientBuilder;

    private MtlsClientFactory factory;

    @BeforeEach
    public void init() {
        factory = new MtlsClientFactory(
                okHttpSimplClient, tokenPropagator, clientProperties, ephemeralProofAdapter, restClientBuilder);
    }

    @Test
    void buildAuthorityClientTest() {
        var url = "http://localhost:8080";
        KeyStore keyStore = mock(KeyStore.class);
        assertDoesNotThrow(() -> factory.buildAuthorityClient(url, keyStore));
    }

    @Test
    void buildParticipantClientTest() {
        var url = "http://localhost:8080";
        KeyStore keyStore = mock(KeyStore.class);
        assertDoesNotThrow(() -> factory.buildParticipantClient(url, keyStore));
    }
}
