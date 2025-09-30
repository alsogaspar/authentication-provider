package eu.europa.ec.simpl.authenticationprovider.services.impl;

import eu.europa.ec.simpl.authenticationprovider.event.OnCredentialUpdateEvent;
import eu.europa.ec.simpl.authenticationprovider.event.OnStoredKeyPair;
import eu.europa.ec.simpl.authenticationprovider.exceptions.CredentialsNotFoundException;
import eu.europa.ec.simpl.authenticationprovider.exceptions.InvalidCredentialException;
import eu.europa.ec.simpl.authenticationprovider.exceptions.RevokedCredentialException;
import eu.europa.ec.simpl.authenticationprovider.services.CredentialService;
import eu.europa.ec.simpl.authenticationprovider.services.KeyPairService;
import eu.europa.ec.simpl.client.util.DaggerCertificateRevocationFactory;
import eu.europa.ec.simpl.common.model.dto.authenticationprovider.CredentialDTO;
import eu.europa.ec.simpl.common.model.dto.identityprovider.ParticipantDTO;
import eu.europa.ec.simpl.common.utils.CredentialUtil;
import eu.europa.ec.simpl.common.utils.Sha384Converter;
import jakarta.annotation.Nullable;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
public abstract class AbstractCredentialService implements CredentialService {
    private final ApplicationEventPublisher publisher;
    private final KeyPairService keyPairService;

    protected AbstractCredentialService(ApplicationEventPublisher publisher, KeyPairService keyPairService) {
        this.publisher = publisher;
        this.keyPairService = keyPairService;
    }

    public abstract Credential saveCredential(Credential credential);

    public abstract boolean hasStoredCredential();

    public abstract List<Credential> findAllCredentials();

    public abstract void deleteStoredCredential();

    @Override
    @Transactional
    public long insert(byte[] fileContent) {
        log.info("CredentialService.insert() start");
        var entity = new Credential();
        entity.setContent(fileContent);
        doDeleteCredential(entity);
        return saveCredential(entity).getId();
    }

    @Override
    public boolean hasCredential() {
        log.info("CredentialService.hasCredential() start");
        if (!hasStoredCredential()) {
            publisher.publishEvent(new OnCredentialUpdateEvent(null));
            return false;
        }
        return true;
    }

    @Override
    public byte[] getCredential() {
        log.info("CredentialService.getCredential() start");
        return getSavedCredentialEntity().getContent();
    }

    @Override
    public eu.europa.ec.simpl.api.authenticationprovider.v1.model.CredentialDTO getCredentialDTO() {
        var credential = getSavedCredentialEntity();
        var keystore =
                CredentialUtil.loadCredential(new ByteArrayInputStream(credential.getContent()), getPrivateKey());
        var publicKey = CredentialUtil.extractPublicKeyFromKeystore(keystore);
        var publicKeyToBase64 = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        var credentialId = Sha384Converter.toSha384(publicKey);
        return new eu.europa.ec.simpl.api.authenticationprovider.v1.model.CredentialDTO()
                .setPublicKey(publicKeyToBase64)
                .setCredentialId(credentialId)
                .setParticipantId(credential.getParticipantId());
    }

    @Override
    @EventListener(OnStoredKeyPair.class)
    public void deleteCredential() {
        doDeleteCredential(null);
    }

    private void doDeleteCredential(@Nullable Credential newCredential) {
        deleteStoredCredential();
        publisher.publishEvent(new OnCredentialUpdateEvent(newCredential));
    }

    @Override
    public CredentialDTO getPublicKey() {
        var keystore = CredentialUtil.loadCredential(new ByteArrayInputStream(getCredential()), getPrivateKey());
        var base64encoded = CredentialUtil.convertPublicKeyToBase64(keystore);
        return new CredentialDTO().setPublicKey(base64encoded);
    }

    @Override
    public String getCredentialId() {
        return getCredentialId(getCredential());
    }

    @Override
    public String getCredentialId(byte[] credentials) {
        var keystore = CredentialUtil.loadCredential(new ByteArrayInputStream(credentials), getPrivateKey());
        var publicKey = CredentialUtil.extractPublicKeyFromKeystore(keystore);
        return Sha384Converter.toSha384(publicKey);
    }

    @Override
    public ParticipantDTO getMyParticipantId() {
        var participantId = getSavedCredentialEntity().getParticipantId();
        return new ParticipantDTO().setId(participantId);
    }

    private Credential getSavedCredentialEntity() {
        return findAllCredentials().stream().findAny().orElseThrow(CredentialsNotFoundException::new);
    }

    private PrivateKey getPrivateKey() {
        var privateKey = keyPairService.getInstalledKeyPair().getPrivateKey();
        return CredentialUtil.loadPrivateKey(privateKey, "EC");
    }

    @Override
    public void validateCredential(String body) {
        X509Certificate certificate = null;

        var certFactory = getCertificateFactory();

        try {
            var inputStream = new ByteArrayInputStream(body.getBytes(StandardCharsets.UTF_8));
            certificate = (X509Certificate) certFactory.generateCertificate(inputStream);
        } catch (CertificateException e) {
            log.error("Failed to parse crendential", e);
            throw new InvalidCredentialException();
        }

        try {
            verifyCertificate(certificate);
        } catch (CertificateException e) {
            log.error("Credential revoked", e);
            throw new RevokedCredentialException();
        }
    }

    public void verifyCertificate(X509Certificate certificate) throws CertificateException {
        var certificateRevocation = DaggerCertificateRevocationFactory.create().get();
        certificateRevocation.verify(certificate);
    }

    @SneakyThrows
    private static @NotNull CertificateFactory getCertificateFactory() {
        return CertificateFactory.getInstance("X.509");
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
