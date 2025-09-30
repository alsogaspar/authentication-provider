package eu.europa.ec.simpl.authenticationprovider.repositories;

import static eu.europa.ec.simpl.common.test.TestUtil.a;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import eu.europa.ec.simpl.authenticationprovider.configurations.vault.VaultProperties;
import eu.europa.ec.simpl.authenticationprovider.services.KeyPairService;
import eu.europa.ec.simpl.authenticationprovider.utils.VaultPathUtils;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.vault.core.VaultKeyValueOperationsSupport.KeyValueBackend;
import org.springframework.vault.core.VaultTemplate;

@ExtendWith(MockitoExtension.class)
public class CredentialVaultRepositoryTest {

    private static final String VAULT_SECRET = "secret-junit";
    private static final String VAULT_SECRET_DATA = VAULT_SECRET + "/data";
    private static final String VAULT_SECRET_METADATA = VAULT_SECRET + "/metadata";
    private static final String CREDENTIAL_PATH = "authenticationprovider-junit/credential";

    @Mock
    private ApplicationEventPublisher publisher;

    @Mock
    private KeyPairService keyPairService;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private VaultTemplate vaultTemplate;

    private CredentialVaultRepository repository;

    @BeforeEach
    public void init() {
        var vaultPathUtils = new VaultPathUtils(new VaultProperties(
                URI.create("http:localhost:8200"),
                new VaultProperties.Authentication("roleId", "secretId", "approle"),
                "secret-junit",
                "authenticationprovider-junit"));
        repository = new CredentialVaultRepository(vaultPathUtils, () -> vaultTemplate);
    }

    @Test
    public void saveCredentialTest() {
        var credentialId = 1L;
        var content = "junit-secret-content".getBytes();
        var credential = a(CredentialVaultRepository.Credential.class);
        credential.setId(null);
        credential.setContent(content);
        assertDoesNotThrow(() -> repository.save(credential));
        verify(vaultTemplate.opsForKeyValue(eq(VAULT_SECRET), eq(KeyValueBackend.KV_2)))
                .put(
                        eq(CREDENTIAL_PATH + "/" + credentialId),
                        argThat(body ->
                                Arrays.equals(((CredentialVaultRepository.Credential) body).getContent(), content)));
    }

    @Test
    public void hasStoredCredentialTest() {
        var credential = a(CredentialVaultRepository.Credential.class);
        var credentialIds = List.of(credential.getId().toString());
        given(vaultTemplate.list(VAULT_SECRET_METADATA + "/" + CREDENTIAL_PATH)).willReturn(credentialIds);
        var result = repository.hasOne();
        assertThat(result).isTrue();
    }

    @Test
    public void findAllCredentialsTest() {
        var content = "junit-secret-content".getBytes();
        var credential = a(CredentialVaultRepository.Credential.class);
        credential.setContent(content);
        var credentialId = credential.getId();
        var credentialIds = List.of(credentialId.toString());
        given(vaultTemplate.list(VAULT_SECRET_METADATA + "/" + CREDENTIAL_PATH)).willReturn(credentialIds);
        given(vaultTemplate
                        .opsForKeyValue(VAULT_SECRET, KeyValueBackend.KV_2)
                        .get(CREDENTIAL_PATH + "/" + credentialId, CredentialVaultRepository.Credential.class)
                        .getData())
                .willReturn(credential);
        var result = repository.findAll();
        assertThat(result).isNotEmpty();
        assertThat(result.getFirst().getContent()).isEqualTo(content);
    }

    @Test
    public void deleteStoredCredentialTest() {
        var content = "junit-secret-content".getBytes();
        var credential = a(CredentialVaultRepository.Credential.class);
        credential.setContent(content);
        var credentialId = credential.getId();
        var credentialIds = List.of(credentialId.toString());
        given(vaultTemplate.list(VAULT_SECRET_METADATA + "/" + CREDENTIAL_PATH)).willReturn(credentialIds);
        repository.deleteAll();
        verify(vaultTemplate).delete(VAULT_SECRET_DATA + "/" + CREDENTIAL_PATH + "/" + credentialId);
        verify(vaultTemplate).delete(VAULT_SECRET_METADATA + "/" + CREDENTIAL_PATH + "/" + credentialId);
    }
}
