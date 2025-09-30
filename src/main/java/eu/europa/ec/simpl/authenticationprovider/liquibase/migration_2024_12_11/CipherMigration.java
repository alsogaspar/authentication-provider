package eu.europa.ec.simpl.authenticationprovider.liquibase.migration_2024_12_11;

import eu.europa.ec.simpl.common.exceptions.RuntimeWrapperException;
import java.io.ByteArrayInputStream;
import java.io.SequenceInputStream;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import liquibase.change.custom.CustomTaskChange;
import liquibase.database.Database;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.CustomChangeException;
import liquibase.exception.SetupException;
import liquibase.exception.ValidationErrors;
import liquibase.resource.ResourceAccessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.util.Assert;

@Slf4j
public class CipherMigration implements CustomTaskChange {

    @Override
    public String getConfirmationMessage() {
        return "";
    }

    @Override
    public void setFileOpener(ResourceAccessor arg0) {
        // Overridden method. No logic implemented.
    }

    @Override
    public void setUp() throws SetupException {
        // Overridden method. No logic implemented.
    }

    @Override
    public ValidationErrors validate(Database arg0) {
        // Overridden method. No logic implemented.
        return null;
    }

    @Override
    public void execute(Database database) throws CustomChangeException {
        log.info("Start migration");
        var conn = ((JdbcConnection) database.getConnection()).getUnderlyingConnection();
        var getAllKeysQuery = "SELECT id, public_key, private_key FROM keypair";
        var updateKeysQuery = "UPDATE keypair SET public_key = ?, private_key = ? WHERE id = ?";
        try (var statement = conn.createStatement()) {
            var resultSet = statement.executeQuery(getAllKeysQuery);
            while (resultSet.next()) {
                var id = resultSet.getObject("id");
                log.info("Migrate key {}", id);
                var publicKey = convertEncryption(resultSet.getBytes("public_key"));
                var privateKey = convertEncryption(resultSet.getBytes("private_key"));
                try (var updateStatement = conn.prepareStatement(updateKeysQuery)) {
                    updateStatement.setBytes(1, publicKey);
                    updateStatement.setBytes(2, privateKey);
                    updateStatement.setObject(3, id);
                    updateStatement.executeUpdate();
                }
            }
        } catch (Exception e) {
            throw new RuntimeWrapperException(e);
        }
        log.info("End migration");
    }

    public byte[] convertEncryption(byte[] cipherMessage) {
        var message = decryptUsingAESECB256(cipherMessage);
        return encryptUsingAESGCMNoPagging(message);
    }

    @SuppressWarnings("java:S5542")
    public byte[] decryptUsingAESECB256(byte[] cipherMessage) {
        final String CIPHER_ALGORITHM = "AES";
        try {
            var cipher = Cipher.getInstance(CIPHER_ALGORITHM, new BouncyCastleProvider());
            cipher.init(Cipher.DECRYPT_MODE, getSecretKey(CIPHER_ALGORITHM));
            return cipher.doFinal(cipherMessage);
        } catch (Exception e) {
            throw new RuntimeWrapperException(e);
        }
    }

    public byte[] encryptUsingAESGCMNoPagging(byte[] message) {
        final int IV_SIZE = 12;
        final int TAG_LENGTH = 128;
        final String CIPHER_ALGORITHM = "AES/GCM/NoPadding";
        try {
            // GCM Parameter spec generation
            var iv = new byte[IV_SIZE];
            var secureRandom = new SecureRandom();
            secureRandom.nextBytes(iv);
            var parameterSpec = new GCMParameterSpec(TAG_LENGTH, iv);

            var cipher = Cipher.getInstance(CIPHER_ALGORITHM, new BouncyCastleProvider());
            cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(CIPHER_ALGORITHM), parameterSpec);

            // Join IV and Cipher message
            var inputStream = new ByteArrayInputStream(message);
            var ivStream = new ByteArrayInputStream(iv);
            try (var cipherInputStream = new CipherInputStream(inputStream, cipher)) {
                var stream = new SequenceInputStream(ivStream, cipherInputStream);
                return IOUtils.toByteArray(stream);
            }
        } catch (Exception e) {
            throw new RuntimeWrapperException(e);
        }
    }

    public String getBase64SecretKeyFromEnv() {
        return System.getenv("CRYPTO_SECRETKEYBASE64");
    }

    private SecretKey getSecretKey(String cipherAlgorithm) {
        String secretKeyBase64 = getBase64SecretKeyFromEnv();
        Assert.notNull(secretKeyBase64, "Property crypto.secretKeyBase64 is mandatory");
        var keyBytes = Base64.getDecoder().decode(secretKeyBase64);
        return new SecretKeySpec(keyBytes, cipherAlgorithm);
    }
}
