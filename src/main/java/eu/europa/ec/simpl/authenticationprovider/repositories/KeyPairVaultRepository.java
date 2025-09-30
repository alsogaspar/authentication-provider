package eu.europa.ec.simpl.authenticationprovider.repositories;

import eu.europa.ec.simpl.authenticationprovider.configurations.vault.VaultTemplateFactory;
import eu.europa.ec.simpl.authenticationprovider.utils.VaultPathUtils;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.vault.core.VaultKeyValueOperationsSupport.KeyValueBackend;
import org.springframework.vault.core.VaultTemplate;

@Log4j2
@Service
@ConditionalOnBean(VaultTemplateFactory.class)
public class KeyPairVaultRepository implements BaseVaultRepository<KeyPairVaultRepository.ApplicantKeyPair> {

    private final VaultPathUtils vaultPathUtils;
    private final VaultTemplateFactory vaultTemplateFactory;

    public KeyPairVaultRepository(VaultPathUtils vaultPathUtils, VaultTemplateFactory vaultTemplateFactory) {
        this.vaultPathUtils = vaultPathUtils;
        this.vaultTemplateFactory = vaultTemplateFactory;
    }

    public void save(ApplicantKeyPair applicantKeyPair) {
        Assert.notNull(applicantKeyPair, "applicantKeyPair is required and cannot be null");
        log.debug("Given credential id: {}", applicantKeyPair.getId());
        var id = applicantKeyPair.getId();
        if (id == null) {
            id = UUID.randomUUID();
        }
        applicantKeyPair.setId(id);
        log.info("Save credential: {}", applicantKeyPair.getId());
        vaultTemplateFactory
                .get()
                .opsForKeyValue(vaultPathUtils.getSecretEngine(), KeyValueBackend.KV_2)
                .put(vaultPathUtils.getKeyPairPath() + "/" + id, applicantKeyPair);
    }

    public Optional<ApplicantKeyPair> getKeyPair() {
        var vaultTemplate = vaultTemplateFactory.get();
        return findAllId(vaultTemplate).stream().findAny().map(id -> vaultTemplate
                .opsForKeyValue(vaultPathUtils.getSecretEngine(), KeyValueBackend.KV_2)
                .get(vaultPathUtils.getKeyPairPath() + "/" + id, ApplicantKeyPair.class)
                .getData());
    }

    public List<ApplicantKeyPair> findAll() {
        var vaultTemplate = vaultTemplateFactory.get();
        return findAllId(vaultTemplate).stream()
                .map(id -> vaultTemplate
                        .opsForKeyValue(vaultPathUtils.getSecretEngine(), KeyValueBackend.KV_2)
                        .get(vaultPathUtils.getKeyPairPath() + "/" + id, ApplicantKeyPair.class)
                        .getData())
                .toList();
    }

    @Override
    public int count() {
        return findAllId(vaultTemplateFactory.get()).size();
    }

    private List<String> findAllId(VaultTemplate vaultTemplate) {
        return findAllId(vaultTemplate, vaultPathUtils.getSecretMetadata() + "/" + vaultPathUtils.getKeyPairPath());
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ApplicantKeyPair {
        private UUID id;
        private byte[] publicKey;
        private byte[] privateKey;
        private Instant creationTimestamp;
    }
}
