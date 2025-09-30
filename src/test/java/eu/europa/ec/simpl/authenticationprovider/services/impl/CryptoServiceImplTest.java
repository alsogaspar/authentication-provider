package eu.europa.ec.simpl.authenticationprovider.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import eu.europa.ec.simpl.authenticationprovider.configurations.CryptoProperties;
import eu.europa.ec.simpl.authenticationprovider.exceptions.CipherException;
import eu.europa.ec.simpl.authenticationprovider.services.CryptoService;
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
            "crypto.secretKeyBase64=Uj2lLjQjLl45+oBACICQWrJp0KwUoPdVROEWI/OlY3g=",
        })
class CryptoServiceImplTest {

    @Autowired
    private CryptoService cryptoService;

    @Test
    void decryptStreamTest_encryptedMessage_correctClearMessage() throws CipherException {
        // Message: "Hello World!"
        // Algorithm: AES/GCM/NoPadding
        // IV Size: 12
        // Tag lenght: 128
        var secretMessage = "mAFxH2MdWIrKlJ+4OzyeznKtiaw+zAdaLRnCG6sCTbf/tIpTBLDhGw==";
        var secretMessageBytes = Base64.getDecoder().decode(secretMessage);
        var messageBytes = cryptoService.decrypt(secretMessageBytes);
        var message = new String(messageBytes);

        assertEquals("Hello World!", message);
    }

    @Test
    void encryptStreamTest_encryptAndDecruptMessage_success() throws CipherException {
        var message = "Hello World!";
        var secret = cryptoService.encrypt(message.getBytes());
        System.out.println("Secret: " + Base64.getEncoder().encodeToString(secret));
        var clearMessage = cryptoService.decrypt(secret);
        assertEquals(message, new String(clearMessage));
    }

    @Test
    public void decrypt_invalidEncryptedMessage_throwCipherException() {
        var message = new byte[0];
        assertThrows(CipherException.class, () -> cryptoService.decrypt(message));
    }
}
