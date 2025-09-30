package eu.europa.ec.simpl.authenticationprovider.services.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import eu.europa.ec.simpl.authenticationprovider.configurations.KeyPairGenerationAlgorithm;
import eu.europa.ec.simpl.authenticationprovider.configurations.SimplProperties;
import eu.europa.ec.simpl.authenticationprovider.exceptions.CipherException;
import eu.europa.ec.simpl.authenticationprovider.services.KeyPairService;
import eu.europa.ec.simpl.common.model.dto.authenticationprovider.DistinguishedNameDTO;
import eu.europa.ec.simpl.common.model.dto.authenticationprovider.KeyPairDTO;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CSRServiceImplTest {

    @Mock
    private KeyPairService keyPairService;

    @Mock
    private KeyPairGenerationAlgorithm keyPairGenerationAlgorithm;

    @Mock
    private SimplProperties simplProperties;

    @Mock
    private SimplProperties.Certificate certificateProperties;

    @InjectMocks
    private CSRServiceImpl csrService;

    private KeyPairDTO keyPairDTO;

    @BeforeEach
    void setUp() throws NoSuchAlgorithmException {
        Security.addProvider(new BouncyCastleProvider());
        when(simplProperties.certificate()).thenReturn(certificateProperties);
        when(certificateProperties.san()).thenReturn("test.domain.com");
        when(keyPairGenerationAlgorithm.algorithm()).thenReturn("ECDSA");
        when(keyPairGenerationAlgorithm.keyLength()).thenReturn(256);
        when(keyPairGenerationAlgorithm.signatureAlgorithm()).thenReturn("SHA256withECDSA");
        // Generate a real EC key pair for testing
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("ECDSA");
        keyPairGenerator.initialize(256);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        keyPairDTO = new KeyPairDTO(
                keyPair.getPublic().getEncoded(), keyPair.getPrivate().getEncoded());
    }

    @Test
    void generateCSR_ShouldCreateValidCSRAndWriteToOutputStream() throws IOException, CipherException {
        // Arrange
        var distinguishedNameDTO = Instancio.create(DistinguishedNameDTO.class);

        when(keyPairService.getInstalledKeyPair()).thenReturn(keyPairDTO);

        // Act
        var response = csrService.generateCSR(distinguishedNameDTO);

        // Assert
        String csrOutput = new String(response, StandardCharsets.UTF_8);
        assertThat(csrOutput)
                .startsWith("-----BEGIN CERTIFICATE REQUEST-----")
                .endsWith("-----END CERTIFICATE REQUEST-----\n")
                .contains("\n")
                .matches("-----BEGIN CERTIFICATE REQUEST-----\n[A-Za-z0-9+/=]+\n-----END CERTIFICATE REQUEST-----\n");

        verify(keyPairService).getInstalledKeyPair();
    }

    @Test
    void generateCSR_ShouldThrowCipherException_WhenKeyPairServiceFails() throws CipherException {
        // Arrange
        var distinguishedNameDTO = new DistinguishedNameDTO();

        when(keyPairService.getInstalledKeyPair())
                .thenThrow(new CipherException("Failed to generate key pair", new RuntimeException()));

        // Act & Assert
        assertThatThrownBy(() -> csrService.generateCSR(distinguishedNameDTO))
                .isInstanceOf(CipherException.class)
                .hasMessage("Failed to generate key pair");
    }
}
