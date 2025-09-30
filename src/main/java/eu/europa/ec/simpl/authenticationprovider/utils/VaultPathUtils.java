package eu.europa.ec.simpl.authenticationprovider.utils;

import eu.europa.ec.simpl.authenticationprovider.configurations.vault.VaultProperties;
import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class VaultPathUtils {
    private final VaultProperties vaultProperties;

    public VaultPathUtils(VaultProperties vaultProperties) {
        this.vaultProperties = vaultProperties;
    }

    @PostConstruct
    private void debugValues() {
        log.debug("Value of vaultSecret: {}", getSecretEngine());
        log.debug("Value of vaultSecretData: {}", getSecretData());
        log.debug("Value of vaultSecretMetadata: {}", getSecretMetadata());
        log.debug("Value of credentialPath: {}", getCredentialPath());
        log.debug("Value of keyPairPath: {}", getKeyPairPath());
    }

    public String getSecretEngine() {
        return vaultProperties.secretEngine();
    }

    public String getSecretData() {
        return getSecretEngine() + "/data";
    }

    public String getSecretMetadata() {
        return getSecretEngine() + "/metadata";
    }

    public String getBasepath() {
        return vaultProperties.basePath();
    }

    public String getCredentialPath() {
        return getBasepath() + "/credential";
    }

    public String getKeyPairPath() {
        return getBasepath() + "/keypair";
    }
}
