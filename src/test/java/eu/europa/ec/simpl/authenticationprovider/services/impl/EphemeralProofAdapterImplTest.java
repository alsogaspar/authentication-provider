package eu.europa.ec.simpl.authenticationprovider.services.impl;

import static eu.europa.ec.simpl.common.test.TestUtil.an;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;

import eu.europa.ec.simpl.authenticationprovider.repositories.EphemeralProofRepository;
import eu.europa.ec.simpl.authenticationprovider.services.CredentialService;
import eu.europa.ec.simpl.authenticationprovider.services.IdentityAttributeService;
import eu.europa.ec.simpl.authenticationprovider.services.KeyPairService;
import eu.europa.ec.simpl.common.ephemeralproof.JwtEphemeralProofParser;
import eu.europa.ec.simpl.common.model.dto.securityattributesprovider.IdentityAttributeDTO;
import eu.europa.ec.simpl.common.redis.entity.EphemeralProof;
import eu.europa.ec.simpl.common.utils.CredentialUtil;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EphemeralProofAdapterImplTest {

    @Mock
    private EphemeralProofRepository ephemeralProofRepository;

    @Mock
    private IdentityAttributeService identityAttributeService;

    @Mock
    private CredentialService credentialService;

    @Mock
    private Function<String, JwtEphemeralProofParser> ephemeralProofParserFactory;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private KeyPairService keyPairService;

    private EphemeralProofAdapterImpl adapter;

    @BeforeEach
    public void init() {
        adapter = new EphemeralProofAdapterImpl(
                ephemeralProofRepository,
                identityAttributeService,
                keyPairService,
                credentialService,
                ephemeralProofParserFactory);
    }

    @Test
    void storeEphemeralProof_success() {
        var ephemeralProof = "ephemeralProof";
        var credentialId = "junit-credential-id";
        given(credentialService.getCredentialId()).willReturn(credentialId);
        var parser = mock(JwtEphemeralProofParser.class);
        given(ephemeralProofParserFactory.apply(any())).willReturn(parser);
        var now = Instant.now();
        var raw = "raw-junit";
        var identityAttribute = an(IdentityAttributeDTO.class);
        var identityAttributes = List.of(identityAttribute);
        given(parser.getExpiration()).willReturn(now);
        given(parser.getRaw()).willReturn(raw);
        given(parser.getIdentityAttributes()).willReturn(identityAttributes);

        adapter.storeEphemeralProof(ephemeralProof);

        verify(ephemeralProofRepository).save(argThat(epf -> {
            assertThat(epf.getCredentialId()).isEqualTo(credentialId);
            assertThat(epf.getContent()).isEqualTo(raw);
            return true;
        }));
        verify(identityAttributeService).updateAssignedIdentityAttributes(eq(identityAttributes));
    }

    @Test
    void loadEphemeralProof_success() {
        var credentialId = "junit-credential-id";
        given(credentialService.getCredentialId()).willReturn(credentialId);
        var ephemeralProof = an(EphemeralProof.class);
        given(ephemeralProofRepository.findById(credentialId)).willReturn(Optional.of(ephemeralProof));

        var result = adapter.loadEphemeralProof().get();

        assertThat(result).isEqualTo(ephemeralProof.getContent());
    }

    @Test
    void getKeyStore_givenCredential_willReturnKeyStore() {
        given(credentialService.hasCredential()).willReturn(true);
        try (var credentialUtiils = mockStatic(CredentialUtil.class)) {
            var privateKey = mock(PrivateKey.class);
            var keyStore = mock(KeyStore.class);
            var privateKeyBytes = new byte[0];
            var credentialBytes = new byte[0];
            given(keyPairService.getInstalledKeyPair().getPrivateKey()).willReturn(privateKeyBytes);
            given(CredentialUtil.loadPrivateKey(eq(privateKeyBytes), eq("EC"))).willReturn(privateKey);
            given(credentialService.getCredential()).willReturn(credentialBytes);
            given(CredentialUtil.loadCredential(any(), eq(privateKey))).willReturn(keyStore);
            var result = adapter.getKeyStore();
            assertThat(result).isEqualTo(keyStore);
        }
    }

    @Test
    void getKeyStore_givenCredentialNotPresent_willThrowIllegalStateException() {
        given(credentialService.hasCredential()).willReturn(false);
        assertThrows(IllegalStateException.class, () -> adapter.getKeyStore());
    }
}
