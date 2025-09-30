package eu.europa.ec.simpl.authenticationprovider.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import eu.europa.ec.simpl.authenticationprovider.exceptions.CipherException;
import eu.europa.ec.simpl.authenticationprovider.exceptions.KeyPairNotFoundException;
import eu.europa.ec.simpl.authenticationprovider.services.KeyPairService;
import eu.europa.ec.simpl.common.model.dto.authenticationprovider.ImportKeyPairDTO;
import eu.europa.ec.simpl.common.model.dto.authenticationprovider.KeyPairDTO;
import java.util.NoSuchElementException;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class KeypairControllerTest {

    @Mock
    private KeyPairService keyPairService;

    @InjectMocks
    private KeypairController keypairController;

    @Nested
    @DisplayName("generateKeyPair")
    class GenerateKeyPairTests {

        @Test
        @DisplayName("Should return 204 when key pair is generated successfully")
        void shouldReturn204WhenKeyPairIsGeneratedSuccessfully() throws CipherException {
            // Given
            doNothing().when(keyPairService).generateAndStoreKeyPair();

            // When
            keypairController.generateKeyPair();

            // Then
            verify(keyPairService, times(1)).generateAndStoreKeyPair();
        }

        @Test
        @DisplayName("Should propagate CipherException when generation fails")
        void shouldPropagateCipherExceptionWhenGenerationFails() throws CipherException {
            // Given
            doThrow(new CipherException("Failed to generate key pair", new RuntimeException()))
                    .when(keyPairService)
                    .generateAndStoreKeyPair();

            // When/Then
            assertThatThrownBy(() -> keypairController.generateKeyPair())
                    .isInstanceOf(CipherException.class)
                    .hasMessage("Failed to generate key pair");
        }
    }

    @Nested
    @DisplayName("importKeyPair")
    class ImportKeyPairTests {

        private ImportKeyPairDTO validImportKeyPairDTO;
        private final String validBase64 = validPemString();

        @BeforeEach
        void setUp() {
            validImportKeyPairDTO = Instancio.create(ImportKeyPairDTO.class);
            validImportKeyPairDTO.setPrivateKey(validBase64);
            validImportKeyPairDTO.setPublicKey(validBase64);
        }

        @Test
        @DisplayName("Should return 204 when key pair is imported successfully")
        void shouldReturn204WhenKeyPairIsImportedSuccessfully() {
            // Given
            doNothing().when(keyPairService).importKeyPair(any(KeyPairDTO.class));

            // When
            keypairController.importKeyPair(validImportKeyPairDTO);

            // Then
            verify(keyPairService, times(1)).importKeyPair(any(KeyPairDTO.class));
        }

        @Test
        @DisplayName("Should handle invalid Base64 input appropriately")
        void shouldHandleInvalidBase64Input() {
            // Given
            ImportKeyPairDTO invalidDTO = Instancio.create(ImportKeyPairDTO.class);
            invalidDTO.setPrivateKey("invalid-base64");
            invalidDTO.setPublicKey("invalid-base64");

            // When/Then
            assertThatThrownBy(() -> keypairController.importKeyPair(invalidDTO))
                    .isInstanceOf(NoSuchElementException.class);
        }
    }

    @Nested
    @DisplayName("getInstalledKeyPair")
    class GetInstalledKeyPairTests {

        @Test
        @DisplayName("Should return 200 with key pair when found")
        void shouldReturn200WithKeyPairWhenFound() {
            // Given
            KeyPairDTO expectedKeyPair = Instancio.create(KeyPairDTO.class);
            when(keyPairService.getInstalledKeyPair()).thenReturn(expectedKeyPair);

            // When
            KeyPairDTO result = keypairController.getInstalledKeyPair();

            // Then
            assertThat(result).isNotNull().isEqualTo(expectedKeyPair);
            verify(keyPairService, times(1)).getInstalledKeyPair();
        }

        @Test
        @DisplayName("Should raise exception when no key pair is found")
        void shouldRaiseExceptionWhenNoKeyPairFound() {
            // Given
            when(keyPairService.getInstalledKeyPair()).thenThrow(new KeyPairNotFoundException());

            // When
            assertThatThrownBy(() -> keypairController.getInstalledKeyPair())
                    .hasMessageContaining("No KeyPair Found, can't generate CSR");
        }
    }

    private byte[] validPemBytes() {
        return validPemString().getBytes();
    }

    private String validPemString() {
        return """
-----BEGIN PRIVATE KEY-----
MIIEuwIBADANBgkqhkiG9w0BAQEFAASCBKUwggShAgEAAoIBAQC1lg/PULMEfvnh
Bpu81FOb2R0fFK1muHUcmOFCARu3AJM6EoW1lzgRYfe0m0+sNdg3ZZHBpE2j7wLe
XSc8yG/CWdNdygvlN1dwPzQWLbmEtfVvkvWnGjOkPmFg5c2Q8m7JPL8n4LM2BEVt
fy4szlwOQGz6tBGR+BkSm2tEvvnvX9L2btPHPHWS2ikHavQkbKcMEgooFa2AZqL/
Q41M5nGUIXRE2Ahrh12GlXftKwRfVxqJmMO0gW3X0Xhk4f+fwCHbjJDbEoO6ADRv
JRaVyzuxBAtXW38NTh9HSaIdg5RNPCbX1xzsljMeDFfXrf6zpUNM6Hgz34qWthN+
Xb3qFDxJAgMBAAECgf8278XDYIjIgvNWXixc6fNV+gFn5NqNU2nFpNv9psLZ1faV
1lBdk12aaJPsR3lrepUg/tiopM29WZcY8whELdgG1okivLsBbIGrjC3KVYkjP1oU
Q4mf6HUxZz2FGhDUSHTNduAFEnr3AIwMUacb+JxuvwF/V6uYL0esb8OM/jZjUuKB
ojpO7HiG7zIOC4gAOHxfCGDMeQ4rzYA8D2WZN06g1WHxXyQdanIOJQ1Dc7pIL23V
xaJ7X4oYmnyZoMA6eyMtlxCgt6jSjvD19EHQgyqEiXCFZrNaGxGCWZ6TLjsTlYI7
+bacNqfKk0szT+CslovwnUFfHuBCUlFTUcxXAiECgYEA27/mNeOm5XciiV0CtW8f
RnMSOXtRUGd4ivj+dnyxKFCceNy72SaoQOj/kndWhTlOBFUnNTjHULOMaSwaQCvp
L93OVez7as/j0hAFMd7hQ5pR9SjZcaMr+cDpZXQV3rJ63seKYtel8uxWfKImuerK
0ybwVG/YLal9IgoGGVdueekCgYEA04qCCJCWjlSL+zLe5ucZ6dDKWy3kTyZYT1C5
0X2XBH3jBdYflpE+kO+erqMnHxvoD+pJHcOBDSeaguYeZERcQLScqV0pkQ0mBFT6
fT+x9JVuGwxXU7R3n0EbvVqskHvamoI141P+fAy9OZapLZ4o/5y3lC5cAeO3uB1J
bKG102ECgYEAg7AqW10DpTuRvNO6TdQ739IRt9TI0/BN8qpog8URvWEhg+Rzxhw+
dDetAK73cHr43+vPxTszZo9Ss6w0RwSJh5/aiO+tc0MF+dqT4mD4ibSvLMXAGTN5
pQAQXjYE7SH0NBFMQxOMZVlwMtxRbNThm0wYdeFYoXSSgpWTNhiJIhECgYBBZuNJ
DprdqloU8edkcDLczUai5g9eQTawXv70O+YC54DW/xqJDKLRYKQozhx8S751nTO5
1gYCMSpeKhNfYJs9DhoZaso9JFmKoVNIgfbc7Y47IpelM3iAbjrLUktj6Ebv2i4a
+I8Hf1DJi0EG740xahx/3c1ocCUO8U8QHPcAQQKBgGF7vuvKfv1MoYA6suT7f8CE
7DL5Q1aDaJYqBZq6KfyUzi1DiOI/mR4Guw5im/FTDDcG8YTSWJLZsH3k3ysBIPdt
s4SRW+Fth2n0YEJcFGmCPVP9v/nN+nj7RzlQwbczM+jUifQ7ZJ6jzHXeeUlZflIf
FwWx6K66lFE82suKjD59
-----END PRIVATE KEY-----
			""";
    }
}
