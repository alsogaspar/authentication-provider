package eu.europa.ec.simpl.authenticationprovider.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import eu.europa.ec.simpl.authenticationprovider.configurations.vault.VaultProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@Import(VaultPathUtils.class)
public class VaultPathUtilsTest {
    @MockitoBean
    private VaultProperties vaultProperties;

    @Autowired
    private VaultPathUtils vaultPathUtils;

    @Test
    void getSecretEngineTest() {
        var secretEngine = "junit-secret-engine";
        given(vaultProperties.secretEngine()).willReturn(secretEngine);
        var result = vaultPathUtils.getSecretEngine();
        assertThat(result).isEqualTo(secretEngine);
    }

    @Test
    void getSecretDataTest() {
        var secretEngine = "junit-secret-engine";
        given(vaultProperties.secretEngine()).willReturn(secretEngine);
        var result = vaultPathUtils.getSecretData();
        assertThat(result).isEqualTo(secretEngine + "/data");
    }

    @Test
    void getSecretMetadataTest() {
        var secretEngine = "junit-secret-engine";
        given(vaultProperties.secretEngine()).willReturn(secretEngine);
        var result = vaultPathUtils.getSecretMetadata();
        assertThat(result).isEqualTo(secretEngine + "/metadata");
    }

    @Test
    void getBasepathTest() {
        var basePath = "junit-base-path";
        given(vaultProperties.basePath()).willReturn(basePath);
        var result = vaultPathUtils.getBasepath();
        assertThat(result).isEqualTo(basePath);
    }

    @Test
    void getCredentialPathTest() {
        var basePath = "junit-base-path";
        given(vaultProperties.basePath()).willReturn(basePath);
        var result = vaultPathUtils.getCredentialPath();
        assertThat(result).isEqualTo(basePath + "/credential");
    }

    @Test
    void getKeyPairPathTest() {
        var basePath = "junit-base-path";
        given(vaultProperties.basePath()).willReturn(basePath);
        var result = vaultPathUtils.getKeyPairPath();
        assertThat(result).isEqualTo(basePath + "/keypair");
    }
}
