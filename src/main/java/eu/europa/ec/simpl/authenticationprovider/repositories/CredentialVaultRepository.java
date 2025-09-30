package eu.europa.ec.simpl.authenticationprovider.repositories;

import eu.europa.ec.simpl.authenticationprovider.configurations.vault.VaultTemplateFactory;
import eu.europa.ec.simpl.authenticationprovider.utils.VaultPathUtils;
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
public class CredentialVaultRepository implements BaseVaultRepository<CredentialVaultRepository.Credential> {

    private final VaultPathUtils vaultPathUtils;
    private final VaultTemplateFactory vaultTemplateFactory;

    public CredentialVaultRepository(VaultPathUtils vaultPathUtils, VaultTemplateFactory vaultTemplateFactory) {
        this.vaultPathUtils = vaultPathUtils;
        this.vaultTemplateFactory = vaultTemplateFactory;
        log.info("Using CredentialVaultRepository");
    }

    public void save(Credential credential) {
        Assert.notNull(credential, "Credential is required and cannot be null");
        log.debug("Given credential id: {}", credential.getId());
        var id = Optional.ofNullable(credential.getId()).orElse(1L);
        credential.setId(id);
        log.info("Save credential: {}", credential.getId());
        vaultTemplateFactory
                .get()
                .opsForKeyValue(vaultPathUtils.getSecretEngine(), KeyValueBackend.KV_2)
                .put(vaultPathUtils.getCredentialPath() + "/" + id, credential);
    }

    public boolean hasOne() {
        log.debug("Ask if exists stored credentials");
        return Optional.ofNullable(vaultTemplateFactory
                        .get()
                        .list(vaultPathUtils.getSecretMetadata() + "/" + vaultPathUtils.getCredentialPath()))
                .map(list -> !list.isEmpty())
                .orElse(false);
    }

    public List<Credential> findAll() {
        log.debug("Ask for all stored credentials");
        var vaultTemplate = vaultTemplateFactory.get();
        return Optional.ofNullable(findAllId(vaultTemplate)).orElse(List.of()).stream()
                .map((String id) -> {
                    log.debug("Exist id: {}", id);
                    var credential = vaultTemplate
                            .opsForKeyValue(vaultPathUtils.getSecretEngine(), KeyValueBackend.KV_2)
                            .get(vaultPathUtils.getCredentialPath() + "/" + id, Credential.class)
                            .getData();
                    log.debug("Find credential: {}", credential.getId());
                    return credential;
                })
                .toList();
    }

    public void deleteAll() {
        var vaultTemplate = vaultTemplateFactory.get();
        findAllId(vaultTemplate).stream().forEach((String id) -> {
            log.info("Delete credential id: {}", id);
            vaultTemplate.delete(vaultPathUtils.getSecretData() + "/" + vaultPathUtils.getCredentialPath() + "/" + id);
            vaultTemplate.delete(
                    vaultPathUtils.getSecretMetadata() + "/" + vaultPathUtils.getCredentialPath() + "/" + id);
        });
    }

    private List<String> findAllId(VaultTemplate vaultTemplate) {
        return findAllId(vaultTemplate, vaultPathUtils.getSecretMetadata() + "/" + vaultPathUtils.getCredentialPath());
    }

    @Override
    public int count() {
        return findAllId(vaultTemplateFactory.get()).size();
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Credential {
        private Long id;
        private byte[] content;
        private UUID participantId;
    }
}
