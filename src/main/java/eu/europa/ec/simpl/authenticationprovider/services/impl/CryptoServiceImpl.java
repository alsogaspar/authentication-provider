package eu.europa.ec.simpl.authenticationprovider.services.impl;

import eu.europa.ec.simpl.authenticationprovider.configurations.CryptoProperties;
import eu.europa.ec.simpl.authenticationprovider.exceptions.CipherException;
import eu.europa.ec.simpl.authenticationprovider.services.CryptoService;
import eu.europa.ec.simpl.common.exceptions.RuntimeWrapperException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.io.IOUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
public class CryptoServiceImpl implements CryptoService {

    private static final int IV_SIZE = 12;
    private static final int TAG_LENGTH = 128;
    private static final String CIPHER_ALGORITHM = "AES/GCM/NoPadding";

    private final SecretKey secretKey;

    public CryptoServiceImpl(CryptoProperties properties) {
        this.secretKey = getSecretKeyFromProperties(properties);
    }

    public InputStream encryptStream(InputStream inputStream) {
        var iv = buildNewInitializationVector();
        var ivStream = new ByteArrayInputStream(iv);
        var cipherStream = cipherStream(Cipher.ENCRYPT_MODE, inputStream, new GCMParameterSpec(TAG_LENGTH, iv));
        return new SequenceInputStream(ivStream, cipherStream);
    }

    private byte[] buildNewInitializationVector() {
        var iv = new byte[IV_SIZE];
        var secureRandom = new SecureRandom();
        secureRandom.nextBytes(iv);
        return iv;
    }

    public InputStream decryptStream(InputStream inputStream) {
        var iv = pollInitializationVector(inputStream);
        return cipherStream(Cipher.DECRYPT_MODE, inputStream, new GCMParameterSpec(TAG_LENGTH, iv));
    }

    private byte[] pollInitializationVector(InputStream inputStream) {
        var iv = new byte[IV_SIZE];
        try {
            if (inputStream.read(iv) != IV_SIZE) {
                throw new CipherException("Unable to read IV");
            }
        } catch (IOException e) {
            throw new CipherException("Unable to read IV", e);
        }
        return iv;
    }

    @Override
    public byte[] encrypt(byte[] data) {
        var byteStream = new ByteArrayInputStream(data);
        var encryptedStream = encryptStream(byteStream);
        return toByteArray(encryptedStream);
    }

    @Override
    public byte[] decrypt(byte[] data) {
        var byteStream = new ByteArrayInputStream(data);
        var decryptedStream = decryptStream(byteStream);
        return toByteArray(decryptedStream);
    }

    private byte[] toByteArray(InputStream inputStream) {
        try {
            return IOUtils.toByteArray(inputStream);
        } catch (Exception e) {
            throw new CipherException("Unable to encrypt data", e);
        }
    }

    private SecretKey getSecretKeyFromProperties(CryptoProperties properties) {
        String secretKeyBase64 = properties.secretKeyBase64();

        Assert.notNull(secretKeyBase64, "Property crypto.secretKeyBase64 is mandatory");

        var keyBytes = Base64.getDecoder().decode(properties.secretKeyBase64());
        return new SecretKeySpec(keyBytes, CIPHER_ALGORITHM);
    }

    public InputStream cipherStream(int mode, InputStream in, GCMParameterSpec parameterSpec) {
        try {
            var cipher = Cipher.getInstance(CIPHER_ALGORITHM, new BouncyCastleProvider());
            cipher.init(mode, this.secretKey, parameterSpec);
            return new CipherInputStream(in, cipher);
        } catch (Exception e) {
            throw new RuntimeWrapperException(e);
        }
    }
}
