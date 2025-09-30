package eu.europa.ec.simpl.authenticationprovider.repositories;

import static eu.europa.ec.simpl.common.test.TestUtil.an;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import eu.europa.ec.simpl.authenticationprovider.repositories.KeyPairVaultRepository.ApplicantKeyPair;
import eu.europa.ec.simpl.authenticationprovider.utils.VaultPathUtils;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.vault.core.VaultKeyValueOperationsSupport.KeyValueBackend;
import org.springframework.vault.core.VaultTemplate;

@ExtendWith(MockitoExtension.class)
public class KeyPairVaultRepositoryTest {
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private VaultPathUtils vaultPathUtils;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private VaultTemplate vaultTemplate;

    private KeyPairVaultRepository repository;

    @BeforeEach
    public void init() {
        repository = new KeyPairVaultRepository(vaultPathUtils, () -> vaultTemplate);
    }

    @Test
    void saveTest() {
        var applicantKeyPair = an(ApplicantKeyPair.class);
        repository.save(applicantKeyPair);
        verify(vaultTemplate.opsForKeyValue(vaultPathUtils.getSecretEngine(), KeyValueBackend.KV_2))
                .put(
                        argThat(path ->
                                path.startsWith(vaultPathUtils.getKeyPairPath() + "/" + applicantKeyPair.getId())),
                        eq(applicantKeyPair));
    }

    @Test
    void getKeyPairTest() {
        var applicantId = "7f87ae76-bd4c-48b7-8bfb-a9b1dccd1bfc";
        var applicantKeyPair = an(ApplicantKeyPair.class);
        given(vaultTemplate.list(vaultPathUtils.getSecretMetadata() + "/" + vaultPathUtils.getKeyPairPath()))
                .willReturn(List.of(applicantId));
        given(vaultTemplate
                        .opsForKeyValue(vaultPathUtils.getSecretEngine(), KeyValueBackend.KV_2)
                        .get(vaultPathUtils.getKeyPairPath() + "/" + applicantId, ApplicantKeyPair.class)
                        .getData())
                .willReturn(applicantKeyPair);

        var result = repository.getKeyPair();
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(applicantKeyPair);
    }

    @Test
    void findAllTest() {
        var applicantId = "7f87ae76-bd4c-48b7-8bfb-a9b1dccd1bfc";
        var applicantKeyPair = an(ApplicantKeyPair.class);
        given(vaultTemplate.list(vaultPathUtils.getSecretMetadata() + "/" + vaultPathUtils.getKeyPairPath()))
                .willReturn(List.of(applicantId));
        given(vaultTemplate
                        .opsForKeyValue(vaultPathUtils.getSecretEngine(), KeyValueBackend.KV_2)
                        .get(vaultPathUtils.getKeyPairPath() + "/" + applicantId, ApplicantKeyPair.class)
                        .getData())
                .willReturn(applicantKeyPair);
        var result = repository.findAll();
        assertThat(result).isNotEmpty();
        assertThat(result.getFirst()).isEqualTo(applicantKeyPair);
    }

    @Test
    void countTest() {
        var applicantId1 = "7f87ae76-bd4c-48b7-8bfb-a9b1dccd1bfc";
        var applicantId2 = "03b74eba-7fce-412a-b45c-577d553cd05a";
        given(vaultTemplate.list(vaultPathUtils.getSecretMetadata() + "/" + vaultPathUtils.getKeyPairPath()))
                .willReturn(List.of(applicantId1, applicantId2));
        repository.count();
    }
}
