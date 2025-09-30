package eu.europa.ec.simpl.authenticationprovider.services.impl;

import eu.europa.ec.simpl.authenticationprovider.configurations.KeyPairGenerationAlgorithm;
import eu.europa.ec.simpl.authenticationprovider.event.OnStoredKeyPair;
import eu.europa.ec.simpl.authenticationprovider.exceptions.CipherException;
import eu.europa.ec.simpl.authenticationprovider.exceptions.InvalidKeyException;
import eu.europa.ec.simpl.authenticationprovider.exceptions.KeyPairNotFoundException;
import eu.europa.ec.simpl.authenticationprovider.services.KeyPairService;
import eu.europa.ec.simpl.common.exceptions.RuntimeWrapperException;
import eu.europa.ec.simpl.common.model.dto.authenticationprovider.KeyPairDTO;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public abstract class AbstractKeyPairService implements KeyPairService {

    private final KeyPairGenerationAlgorithm keyPairGenerationAlgorithm;
    private final ApplicationEventPublisher applicationEventPublisher;

    protected AbstractKeyPairService(
            KeyPairGenerationAlgorithm keyPairGenerationAlgorithm,
            ApplicationEventPublisher applicationEventPublisher) {
        this.keyPairGenerationAlgorithm = keyPairGenerationAlgorithm;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    protected abstract void save(ApplicantKeyPair applicantKeyPair);

    protected abstract Optional<ApplicantKeyPair> getKeyPair();

    @Override
    @Transactional
    public void generateAndStoreKeyPair() throws CipherException {
        var keyPair = generateKeyPair();
        storeKeyPair(keyPair);
    }

    @Override
    public void importKeyPair(KeyPairDTO keypairDTO) {

        var algorithm = keyPairGenerationAlgorithm.algorithm();

        var privateKeyBytes = keypairDTO.getPrivateKey();
        var privateKey = new PKCS8EncodedKeySpec(privateKeyBytes);

        var publicKeyBytes = keypairDTO.getPublicKey();
        var publicKey = new X509EncodedKeySpec(publicKeyBytes);

        try {
            var keyFactory = KeyFactory.getInstance(algorithm);
            var keyPair = new KeyPair(keyFactory.generatePublic(publicKey), keyFactory.generatePrivate(privateKey));

            if (!keyPair.getPrivate().getAlgorithm().equals(algorithm)) {
                throw new InvalidKeyException(String.format(
                        "Invalid key value algorithm. Supported algorithm %s. Private key algorithm: %s, Public key algorithm %s",
                        algorithm, privateKey.getAlgorithm(), publicKey.getAlgorithm()));
            }

            storeKeyPair(keyPair);
        } catch (InvalidKeySpecException e) {
            throw new InvalidKeyException(String.format(
                    "Invalid key specification. Unable to process keys. Supported algorithm %s.", algorithm));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeWrapperException(e);
        }
    }

    @Override
    public Boolean existsKeyPair() {
        var keypair = getKeyPair();
        if (keypair.isEmpty()) {
            throw new KeyPairNotFoundException("No keypair installed");
        }
        return true;
    }

    @Override
    public KeyPairDTO getInstalledKeyPair() {
        var applicantKeyPair = getKeyPair().orElseThrow(KeyPairNotFoundException::new);
        return new KeyPairDTO(applicantKeyPair.getPublicKey(), applicantKeyPair.getPrivateKey());
    }

    private void storeKeyPair(KeyPair keyPair) throws CipherException {
        var applicantKeyPair = new ApplicantKeyPair();
        applicantKeyPair.setPublicKey(keyPair.getPublic().getEncoded());
        applicantKeyPair.setPrivateKey(keyPair.getPrivate().getEncoded());
        save(applicantKeyPair);

        // TODO deleteCredentials is only a part of the steps required to disable the participant from the data-space
        // TODO: clean the caches of roles, identity attributes, ephemeral proof etc...
        applicationEventPublisher.publishEvent(new OnStoredKeyPair());
    }

    private KeyPair generateKeyPair() {
        try {
            var keyPairGenerator = KeyPairGenerator.getInstance(keyPairGenerationAlgorithm.algorithm());
            keyPairGenerator.initialize(keyPairGenerationAlgorithm.keyLength());
            return keyPairGenerator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeWrapperException(e);
        }
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
