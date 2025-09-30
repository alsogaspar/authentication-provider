package eu.europa.ec.simpl.authenticationprovider.utils;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import eu.europa.ec.simpl.common.exceptions.RuntimeWrapperException;
import eu.europa.ec.simpl.common.utils.PemUtil;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

class PemUtilsTest {

    @Test
    void decodePemFormatTest_success() {
        var pem =
                """
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
        var message = assertDoesNotThrow(() -> PemUtils.decodePemFormat(pem));
        var shaMessage = shaMessage(message);

        assertEquals("HJcyqBKL2K3yDaJiC/elHHaV+JM0rIVyMjyUmzDBCVA=", shaMessage);
    }

    String shaMessage(byte[] inputData) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        byte[] hash = digest.digest(inputData);
        return Base64.getEncoder().encodeToString(hash);
    }

    @Test
    void decodePemFormat_IOException() throws IOException {
        var mockInputStream = mock(InputStream.class);
        when(mockInputStream.readAllBytes()).thenThrow(new IOException("Exception"));

        try (MockedStatic<PemUtil> mockedPemUtil = Mockito.mockStatic(PemUtil.class)) {
            mockedPemUtil
                    .when(() -> PemUtil.loadPemObjects(any(InputStream.class)))
                    .thenAnswer(invocation -> List.of(mockInputStream));

            var pem = "certificate";
            assertThrows(RuntimeWrapperException.class, () -> {
                PemUtils.decodePemFormat(pem);
            });
        }
    }
}
