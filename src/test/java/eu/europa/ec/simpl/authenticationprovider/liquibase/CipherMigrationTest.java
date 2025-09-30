package eu.europa.ec.simpl.authenticationprovider.liquibase;

import static org.assertj.core.api.Assertions.assertThat;

import eu.europa.ec.simpl.authenticationprovider.configurations.CryptoProperties;
import eu.europa.ec.simpl.authenticationprovider.liquibase.migration_2024_12_11.CipherMigration;
import eu.europa.ec.simpl.authenticationprovider.services.CryptoService;
import eu.europa.ec.simpl.authenticationprovider.services.impl.CryptoServiceImpl;
import java.util.Base64;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@Import(CryptoServiceImpl.class)
@EnableConfigurationProperties(CryptoProperties.class)
@TestPropertySource(
        properties = {
            "crypto.secretKeyBase64=" + CipherMigrationTest.SECRET_KEY,
        })
public class CipherMigrationTest {

    public static final String SECRET_KEY = "Uj2lLjQjLl45+oBACICQWrJp0KwUoPdVROEWI/OlY3g=";

    @Autowired
    private CryptoService cryptoService;

    @Test
    public void migrationChainTest_expectedSuccessEncryptionAndDescryption() {
        // echo -n "Hello World!" | openssl enc -aes-256-ecb -K
        // 523DA52E34232E5E39FA80400880905AB269D0AC14A0F75544E11623F3A56378 -nosalt  | base64
        var originalMessage = Base64.getDecoder().decode("/tqMAbBPNhJGeXvYdsFaDg==");
        byte[] finalMessage;

        // All migration cipher transofrmation
        CipherMigration migration20241211 = getMigration20241211();

        // Chain migration cipher transformation
        finalMessage = migration20241211.convertEncryption(originalMessage);

        var message = new String(cryptoService.decrypt(finalMessage));
        assertThat(message).isEqualTo("Hello World!");
    }

    private CipherMigration getMigration20241211() {
        return new CipherMigration() {
            @Override
            public String getBase64SecretKeyFromEnv() {
                return SECRET_KEY;
            }
        };
    }
}
