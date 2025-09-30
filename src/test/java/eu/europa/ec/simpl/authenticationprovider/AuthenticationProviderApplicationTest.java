package eu.europa.ec.simpl.authenticationprovider;

import static org.assertj.core.api.Assertions.assertThat;

import eu.europa.ec.simpl.api.identityprovider.v1.exchanges.MtlsApi;
import eu.europa.ec.simpl.api.usersroles.v1.exchanges.IdentityAttributesApi;
import eu.europa.ec.simpl.authenticationprovider.repositories.CredentialRepository;
import eu.europa.ec.simpl.authenticationprovider.repositories.EphemeralProofRepository;
import eu.europa.ec.simpl.authenticationprovider.repositories.IdentityAttributeWithOwnershipRepository;
import eu.europa.ec.simpl.authenticationprovider.repositories.KeyPairRepository;
import eu.europa.ec.simpl.authenticationprovider.services.CredentialInitializer;
import eu.europa.ec.simpl.common.exchanges.usersroles.CredentialExchange;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@SpringBootMockMvcTest
@Disabled
class AuthenticationProviderApplicationTest {

    @MockitoBean
    private CredentialRepository credentialRepository;

    @MockitoBean
    private EphemeralProofRepository ephemeralProofRepository;

    @MockitoBean
    private IdentityAttributeWithOwnershipRepository identityAttributeWithOwnershipRepository;

    @MockitoBean
    private KeyPairRepository keyPairRepository;

    @MockitoBean
    private MtlsApi tierOnePublicKeyApi;

    @MockitoBean
    private CredentialExchange credentialExchange;

    @MockitoBean
    private IdentityAttributesApi usersRolesIdentityAttributesApi;

    @MockitoBean
    private SslBundles sslBundles;

    @MockitoBean
    private CredentialInitializer credentialInitializer;

    @Autowired
    private ApplicationContext context;

    @Test
    void contextLoads() {
        assertThat(context).isNotNull();
    }
}
