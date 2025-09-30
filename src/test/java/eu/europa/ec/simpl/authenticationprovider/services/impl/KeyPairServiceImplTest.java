package eu.europa.ec.simpl.authenticationprovider.services.impl;

import static eu.europa.ec.simpl.common.test.TestUtil.an;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.BDDAssertions.fail;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.times;
import static org.mockito.BDDMockito.when;
import static org.mockito.Mockito.verify;

import eu.europa.ec.simpl.authenticationprovider.configurations.CryptoProperties;
import eu.europa.ec.simpl.authenticationprovider.configurations.KeyPairGenerationAlgorithm;
import eu.europa.ec.simpl.authenticationprovider.entities.ApplicantKeyPair;
import eu.europa.ec.simpl.authenticationprovider.exceptions.CipherException;
import eu.europa.ec.simpl.authenticationprovider.exceptions.InvalidKeyException;
import eu.europa.ec.simpl.authenticationprovider.exceptions.KeyPairNotFoundException;
import eu.europa.ec.simpl.authenticationprovider.mappers.KeyPairServiceMapperImpl;
import eu.europa.ec.simpl.authenticationprovider.repositories.KeyPairRepository;
import eu.europa.ec.simpl.authenticationprovider.services.CryptoService;
import eu.europa.ec.simpl.authenticationprovider.services.KeyPairService;
import eu.europa.ec.simpl.common.exceptions.RuntimeWrapperException;
import eu.europa.ec.simpl.common.model.dto.authenticationprovider.KeyPairDTO;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Collections;
import java.util.List;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@Import({KeyPairDatabaseServiceImpl.class, CryptoServiceImpl.class, KeyPairServiceMapperImpl.class})
@EnableConfigurationProperties({KeyPairGenerationAlgorithm.class, CryptoProperties.class})
@TestPropertySource(
        properties = {
            "keypair.algorithm=ECDSA",
            "keypair.keyLength=256",
            "crypto.secretKeyBase64=Uj2lLjQjLl45+oBACICQWrJp0KwUoPdVROEWI/OlY3g=",
            "security.secret.location=database",
        })
class KeyPairServiceImplTest {

    @MockitoBean
    private KeyPairRepository keyPairRepository;

    @Autowired
    private CryptoService cryptoService;

    @Autowired
    private KeyPairService keyPairService;

    @MockitoSpyBean
    private KeyPairGenerationAlgorithm keyPairGenerationAlgorithm;

    @BeforeAll
    public static void addProvider() {
        Security.addProvider(new BouncyCastleProvider());
    }

    @Test
    void importKeyPair_success() {
        var keyPairDTO = generateKeyPairDTO(generateKeys(keyPairGenerationAlgorithm.algorithm()));

        var privateKey = keyPairDTO.getPrivateKey();
        var publicKey = keyPairDTO.getPublicKey();

        keyPairService.importKeyPair(keyPairDTO);

        verify(keyPairRepository).save(argThat(applicantKeyPair -> {
            assertThat(cryptoService.decrypt(applicantKeyPair.getPrivateKey())).isEqualTo(privateKey);
            assertThat(cryptoService.decrypt(applicantKeyPair.getPublicKey())).isEqualTo(publicKey);
            return true;
        }));
    }

    @Test
    void importKeyPair_usingAlgorithmNotConfigured_shouldThrowInvalidKeyException() {
        var keyPairDTO = generateKeyPairDTO(generateKeys("RSA"));
        assertThrows(InvalidKeyException.class, () -> keyPairService.importKeyPair(keyPairDTO));
    }

    private KeyPair generateKeys(String algorithm) {
        try {
            var kpf = KeyPairGenerator.getInstance(algorithm);
            return kpf.generateKeyPair();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private KeyPairDTO generateKeyPairDTO(KeyPair keyPair) {
        var privateKey = keyPair.getPrivate().getEncoded();
        var publicKey = keyPair.getPublic().getEncoded();
        return new KeyPairDTO(publicKey, privateKey);
    }

    @Test
    void generateAndStoreKeyPair_success() {
        var algorithm = keyPairGenerationAlgorithm.algorithm();
        var keyLength = keyPairGenerationAlgorithm.keyLength();

        keyPairService.generateAndStoreKeyPair();

        // Then the repository should save an ApplicantKeyPair with valid keys
        then(keyPairRepository).should().save(argThat(applicantKeyPair -> {
            // Decrypt and verify that the keys are generated and encrypted correctly
            var decryptedPrivateKey = cryptoService.decrypt(applicantKeyPair.getPrivateKey());
            var decryptedPublicKey = cryptoService.decrypt(applicantKeyPair.getPublicKey());

            try {
                var keyFactory = KeyFactory.getInstance(algorithm);
                var privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(decryptedPrivateKey));
                var publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(decryptedPublicKey));

                // Verify that the algorithm is correct
                assertThat(privateKey.getAlgorithm()).isEqualTo(algorithm);
                assertThat(publicKey.getAlgorithm()).isEqualTo(algorithm);

                // Verify that the key sizes conform to the configuration
                var keyPairGenerator = KeyPairGenerator.getInstance(algorithm);
                keyPairGenerator.initialize(keyLength);
                var generatedKeyPair = keyPairGenerator.generateKeyPair();
                assertThat(privateKey.getEncoded())
                        .hasSameSizeAs(generatedKeyPair.getPrivate().getEncoded());
                assertThat(publicKey.getEncoded())
                        .hasSameSizeAs(generatedKeyPair.getPublic().getEncoded());

                return true;
            } catch (Exception e) {
                fail("Test failed: " + e.getMessage());
                return false;
            }
        }));

        then(keyPairRepository).should(times(1)).save(any(ApplicantKeyPair.class));
    }

    @Test
    void generateAndStoreKeyPair_shouldThrowRuntimeWrapperException_whenNoSuchAlgorithmExceptionOccurs()
            throws CipherException {
        given(keyPairGenerationAlgorithm.algorithm()).willReturn("InvalidAlgorithm");

        assertThatThrownBy(() -> keyPairService.generateAndStoreKeyPair())
                .isInstanceOf(RuntimeWrapperException.class)
                .hasCauseInstanceOf(NoSuchAlgorithmException.class);
    }

    @Test
    void getInstalledKeyPair_shouldReturnDecryptedKeyPair_whenKeyPairExists() {
        var publicKey = "decryptedPublicKey".getBytes();
        var privateKey = "decryptedPrivateKey".getBytes();
        var encryptedPublicKey = cryptoService.encrypt(publicKey);
        var encryptedPrivateKey = cryptoService.encrypt(privateKey);
        var applicantKeyPair = new ApplicantKeyPair(encryptedPublicKey, encryptedPrivateKey);
        given(keyPairRepository.findAllByOrderByCreationTimestampDesc()).willReturn(List.of(applicantKeyPair));

        var result = keyPairService.getInstalledKeyPair();

        assertThat(result).isNotNull();
        assertThat(result.getPublicKey()).isEqualTo(publicKey);
        assertThat(result.getPrivateKey()).isEqualTo(privateKey);

        then(keyPairRepository).should(times(1)).findAllByOrderByCreationTimestampDesc();
    }

    @Test
    void getInstalledKeyPair_shouldThrowKeyPairNotFoundException_whenNoKeyPairExists() {
        when(keyPairRepository.findAllByOrderByCreationTimestampDesc()).thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> keyPairService.getInstalledKeyPair()).isInstanceOf(KeyPairNotFoundException.class);

        verify(keyPairRepository).findAllByOrderByCreationTimestampDesc();
    }

    @Test
    void existsKeyPair_giveValidKeyPair_willReturnTrue() {
        var applicantKeyPair = an(ApplicantKeyPair.class);
        var encryptedPublicKey = cryptoService.encrypt(applicantKeyPair.getPublicKey());
        applicantKeyPair.setPublicKey(encryptedPublicKey);
        var encryptedPrivateKey = cryptoService.encrypt(applicantKeyPair.getPrivateKey());
        applicantKeyPair.setPrivateKey(encryptedPrivateKey);
        given(keyPairRepository.findAllByOrderByCreationTimestampDesc()).willReturn(List.of(applicantKeyPair));
        var result = keyPairService.existsKeyPair();
        assertThat(result).isTrue();
    }

    @Test
    void existsKeyPair_giveNotPresent_willThrowKeyPairNotFoundException() {
        given(keyPairRepository.findAllByOrderByCreationTimestampDesc()).willReturn(List.of());
        assertThrows(KeyPairNotFoundException.class, () -> keyPairService.existsKeyPair());
    }
}
