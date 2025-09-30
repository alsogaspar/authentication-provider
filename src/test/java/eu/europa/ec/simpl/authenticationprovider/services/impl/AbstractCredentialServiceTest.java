package eu.europa.ec.simpl.authenticationprovider.services.impl;

import static eu.europa.ec.simpl.common.test.TestCertificateUtil.anEncodedKeystore;
import static eu.europa.ec.simpl.common.test.TestCertificateUtil.generateKeyPair;
import static eu.europa.ec.simpl.common.test.TestUtil.a;
import static eu.europa.ec.simpl.common.test.TestUtil.an;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.withSettings;

import eu.europa.ec.simpl.authenticationprovider.configurations.mtls.MtlsClientFactory;
import eu.europa.ec.simpl.authenticationprovider.configurations.mtls.MtlsClientProperties;
import eu.europa.ec.simpl.authenticationprovider.event.OnCredentialUpdateEvent;
import eu.europa.ec.simpl.authenticationprovider.exceptions.CredentialsNotFoundException;
import eu.europa.ec.simpl.authenticationprovider.exceptions.RevokedCredentialException;
import eu.europa.ec.simpl.authenticationprovider.services.KeyPairService;
import eu.europa.ec.simpl.common.model.dto.authenticationprovider.KeyPairDTO;
import eu.europa.ec.simpl.common.security.JwtService;
import eu.europa.ec.simpl.common.utils.CredentialUtil;
import java.io.ByteArrayInputStream;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.ECPublicKey;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

@ExtendWith(MockitoExtension.class)
class AbstractCredentialServiceTest {

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    JwtService jwtService;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    MtlsClientFactory mtlsClientFactory;

    @Mock
    KeyPairService keyPairService;

    @Mock
    ApplicationEventPublisher publisher;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    MtlsClientProperties mtlsClientProperties;

    AbstractCredentialService credentialService;

    @BeforeAll
    static void setup() {
        Security.addProvider(new BouncyCastleProvider());
    }

    @BeforeEach
    public void init() {
        credentialService = mock(
                AbstractCredentialService.class,
                withSettings().useConstructor(publisher, keyPairService).defaultAnswer(Answers.CALLS_REAL_METHODS));
    }

    @Test
    void insert_withGivenBytes_shouldSaveCorrespondingCredential()
            throws CertificateException, NoSuchAlgorithmException, OperatorCreationException {
        var content = a(byte[].class);
        var expectedCredential = a(AbstractCredentialService.Credential.class);
        // Given
        expectedCredential.setContent(getAnEncodedKeystore(generateKeyPair()));
        given(credentialService.saveCredential(any())).willAnswer(i -> {
            AbstractCredentialService.Credential arg = i.getArgument(0);
            arg.setId(10L);
            return arg;
        });

        // When
        credentialService.insert(content);

        // Then
        then(credentialService).should().saveCredential(argThat(entity -> Arrays.equals(entity.getContent(), content)));
    }

    @Test
    void hasCredential_whenRepositoryDoesNotContainAnyCredential_shouldReturnFalse() {
        // Given
        given(credentialService.hasStoredCredential()).willReturn(false);
        // When
        var hasCredential = credentialService.hasCredential();
        // Then
        assertThat(hasCredential).isFalse();
    }

    @Test
    void hasCredential_whenRepositoryDoesNotContainsTheCredential_shouldReturnTrue() {
        // Given
        given(credentialService.hasStoredCredential()).willReturn(true);
        // When
        var hasCredential = credentialService.hasCredential();
        // Then
        assertThat(hasCredential).isTrue();
    }

    @Test
    void getCredential_whenCredentialIsPresent_willReturnTheCredential() {
        var expectedCredential = a(AbstractCredentialService.Credential.class);
        // Given
        given(credentialService.findAllCredentials()).willReturn(List.of(expectedCredential));
        // When
        var actualCredential = credentialService.getCredential();
        // Then
        assertThat(actualCredential).isEqualTo(expectedCredential.getContent());
    }

    @Test
    void getCredential_whenCredentialIsPresent_willThrowCredentialsNotFoundException() {
        // Given
        given(credentialService.findAllCredentials()).willReturn(List.of());
        // When
        var exception = catchException(() -> credentialService.getCredential());
        // Then
        assertThat(exception).isInstanceOf(CredentialsNotFoundException.class);
    }

    @Test
    void deleteCredential() {
        // Given
        doNothing().when(credentialService).deleteStoredCredential();
        // When
        credentialService.deleteCredential();
        // Then
        then(credentialService).should().deleteStoredCredential();
        then(publisher).should().publishEvent(any(OnCredentialUpdateEvent.class));
    }

    @Test
    @Disabled // TODO Enable test
    void getPublicKey() throws CertificateException, NoSuchAlgorithmException, OperatorCreationException {
        // Given
        var keyPair = generateKeyPair();
        // TODO mock keyPairService.getPrivateKey()
        // given(keyPairService.getPrivateKey()).willReturn(keyPair.getPrivate());
        var encodedKeystore = getAnEncodedKeystore(keyPair);
        var keyStore = CredentialUtil.loadCredential(new ByteArrayInputStream(encodedKeystore), keyPair.getPrivate());
        var credentialMock = new AbstractCredentialService.Credential();
        credentialMock.setContent(encodedKeystore);
        given(credentialService.findAllCredentials()).willReturn(List.of(credentialMock));

        // When
        var credential = credentialService.getPublicKey();

        // Then
        then(credentialService).should().findAllCredentials();
        var publicKey = CredentialUtil.extractPublicKeyFromKeystore(keyStore);
        var encoded = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        assertThat(credential.getPublicKey()).isEqualTo(encoded);
    }

    private static byte[] getAnEncodedKeystore(KeyPair keyPair) throws OperatorCreationException, CertificateException {
        return anEncodedKeystore("CN=OnBoardingCA", "CN=applicant@applicant.com, O=Organization", keyPair);
    }

    @Test
    void getMyParticipantId() throws CertificateException, NoSuchAlgorithmException, OperatorCreationException {
        var expectedParticipantId = an(UUID.class);
        // Given
        var encodedKeystore = anEncodedKeystore(
                "CN=OnBoardingCA", "CN=%s, O=Organization".formatted(expectedParticipantId), generateKeyPair());
        var credentialMock = new AbstractCredentialService.Credential();
        credentialMock.setContent(encodedKeystore);
        credentialMock.setParticipantId(expectedParticipantId);
        given(credentialService.findAllCredentials()).willReturn(List.of(credentialMock));

        // When
        var actualParticipantId = credentialService.getMyParticipantId().getId();

        // Then
        then(credentialService).should().findAllCredentials();
        assertThat(actualParticipantId).isEqualTo(expectedParticipantId);
    }

    @Test
    void validateCredential_givenValidCertificate_doNotThrowException() throws CertificateException {
        try (var certificateFactory = mockStatic(CertificateFactory.class, Answers.RETURNS_DEEP_STUBS)) {
            var body = "junit-body";
            var certificate = mock(X509Certificate.class);
            var certFactory = CertificateFactory.getInstance("X.509");
            given(certFactory.generateCertificate(
                            argThat(stream -> body.equals(new String(((ByteArrayInputStream) stream).readAllBytes())))))
                    .willReturn(certificate);
            doNothing().when(credentialService).verifyCertificate(certificate);
            credentialService.validateCredential(body);
            verify(credentialService).verifyCertificate(certificate);
        }
    }

    @Test
    void validateCredential_givenInvalidCertificate_doThrowInvalidCredentialException() throws CertificateException {
        try (var certificateFactory = mockStatic(CertificateFactory.class, Answers.RETURNS_DEEP_STUBS)) {
            var body = "junit-body";
            var certFactory = CertificateFactory.getInstance("X.509");
            var certificate = mock(X509Certificate.class);
            given(certFactory.generateCertificate(any())).willReturn(certificate);
            doThrow(CertificateException.class).when(credentialService).verifyCertificate(certificate);
            assertThrows(RevokedCredentialException.class, () -> credentialService.validateCredential(body));
        }
    }

    @Test
    void getCredentialDTO_givenSavedCredentialEntity_returnCredentialDTO() {
        var credential = a(AbstractCredentialService.Credential.class);
        var credentials = List.of(credential);
        byte[] credentialBytes = {};
        byte[] encodedPrivateKey = {};
        var keyPair = mock(KeyPairDTO.class, Answers.RETURNS_DEEP_STUBS);
        var privateKey = mock(PrivateKey.class);
        willReturn(credentials).given(credentialService).findAllCredentials();
        given(keyPairService.getInstalledKeyPair()).willReturn(keyPair);
        given(keyPair.getPrivateKey()).willReturn(encodedPrivateKey);

        try (var credentialUtil = mockStatic(CredentialUtil.class)) {
            var keyStore = mock(KeyStore.class);
            given(CredentialUtil.loadPrivateKey(encodedPrivateKey, "EX")).willReturn(privateKey);
            given(CredentialUtil.loadCredential(
                            argThat(stream ->
                                    Arrays.equals(((ByteArrayInputStream) stream).readAllBytes(), credentialBytes)),
                            eq(privateKey)))
                    .willReturn(keyStore);
            ECPublicKey ecPublicKey = mock(ECPublicKey.class);
            given(CredentialUtil.extractPublicKeyFromKeystore(any())).willReturn(ecPublicKey);
            given(ecPublicKey.getEncoded()).willReturn("encodedPublicKey".getBytes());
            credentialService.getCredentialDTO();
        }
    }
}
