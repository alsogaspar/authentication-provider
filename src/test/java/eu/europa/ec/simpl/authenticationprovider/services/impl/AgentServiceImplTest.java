package eu.europa.ec.simpl.authenticationprovider.services.impl;

import static eu.europa.ec.simpl.common.test.TestUtil.a;
import static eu.europa.ec.simpl.common.test.TestUtil.an;
import static eu.europa.ec.simpl.common.test.TestUtil.anURI;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import eu.europa.ec.simpl.authenticationprovider.configurations.mtls.MtlsClientBuilder;
import eu.europa.ec.simpl.authenticationprovider.exceptions.InvalidFqdnException;
import eu.europa.ec.simpl.authenticationprovider.services.CredentialId;
import eu.europa.ec.simpl.authenticationprovider.services.CredentialService;
import eu.europa.ec.simpl.authenticationprovider.services.IdentityAttributeService;
import eu.europa.ec.simpl.client.core.adapters.EphemeralProofAdapter;
import eu.europa.ec.simpl.common.constants.Roles;
import eu.europa.ec.simpl.common.exchanges.mtls.AuthorityExchange;
import eu.europa.ec.simpl.common.model.dto.authenticationprovider.EchoDTO;
import eu.europa.ec.simpl.common.model.dto.identityprovider.ParticipantWithIdentityAttributesDTO;
import eu.europa.ec.simpl.common.model.dto.securityattributesprovider.IdentityAttributeDTO;
import eu.europa.ec.simpl.common.model.dto.securityattributesprovider.IdentityAttributeWithOwnershipDTO;
import eu.europa.ec.simpl.common.security.JwtService;
import feign.RetryableException;
import java.util.List;
import java.util.Optional;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.util.UriComponentsBuilder;

@ExtendWith(MockitoExtension.class)
class AgentServiceImplTest {

    @Mock
    AuthorityExchange authorityExchange;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    MtlsClientBuilder mtlsClientBuilder;

    @Mock
    IdentityAttributeService identityAttributeService;

    @Mock
    JwtService jwtService;

    @Mock
    CredentialService credentialService;

    @Mock
    EphemeralProofAdapter ephemeralProofAdapter;

    @InjectMocks
    AgentServiceImpl agentService;

    @Test
    void getAndSyncIdentityAttributes() {
        var expectedIdAElement = a(IdentityAttributeWithOwnershipDTO.class);
        var expectedIdA = List.of(expectedIdAElement);

        // Given
        given(authorityExchange.getIdentityAttributesWithOwnership()).willReturn(expectedIdA);
        // When
        agentService.getAndSyncIdentityAttributes();
        // Then
        then(authorityExchange).should().getIdentityAttributesWithOwnership();
        then(identityAttributeService).should().overwriteIdentityAttributes(expectedIdA);
    }

    @Test
    void getParticipantIdentityAttributes() {
        var expectedIdAElement = a(IdentityAttributeDTO.class);
        var expectedIdA = List.of(expectedIdAElement);

        // Given
        String certificateId = Instancio.gen().hash().get();
        given(authorityExchange.getIdentityAttributesByCredentialIdInUri(certificateId))
                .willReturn(expectedIdA);

        // When
        var actualIdA = agentService.getParticipantIdentityAttributes(CredentialId.decode(certificateId));

        // Then
        then(authorityExchange).should().getIdentityAttributesByCredentialIdInUri(certificateId);
        assertThat(actualIdA).isEqualTo(expectedIdA);
    }

    @Test
    void echo_withoutCredential_shouldReturnNotConnectedAndNotSecured() {
        testEcho(
                false,
                Instancio.gen().booleans().get(),
                EchoDTO.ConnectionStatus.NOT_CONNECTED,
                EchoDTO.MTLSStatus.NOT_SECURED,
                false);
    }

    @Test
    void echo_withCredential_withoutRoleONBOADER_M_shouldNotReturnParticipantData() {
        testEcho(true, false, EchoDTO.ConnectionStatus.CONNECTED, EchoDTO.MTLSStatus.SECURED, false);
    }

    @Test
    void echo_withCredential_withRoleONBOADER_M_shouldReturnParticipantData() {
        testEcho(true, true, EchoDTO.ConnectionStatus.CONNECTED, EchoDTO.MTLSStatus.SECURED, true);
    }

    @Test
    void echo_throwRetryableException_shouldReturnConnectedNotSecured() {
        EchoDTO expectedEcho = an(EchoDTO.class);
        given(jwtService.getEmail()).willReturn(expectedEcho.getEmail());
        given(jwtService.getUsername()).willReturn(expectedEcho.getUsername());
        given(credentialService.hasCredential()).willReturn(true);
        given(authorityExchange.echo()).willThrow(RetryableException.class);

        var identityAttributes = List.of("CodeIA_004", "CodeIA_005");
        given(jwtService.getIdentityAttributes()).willReturn(identityAttributes);

        var response = agentService.echo();

        assertThat(response.getMtlsStatus()).isEqualTo(EchoDTO.MTLSStatus.SECURED);
        assertThat(response.getConnectionStatus()).isEqualTo(EchoDTO.ConnectionStatus.NOT_CONNECTED);
    }

    void testEcho(
            Boolean hasCredential,
            Boolean hasOnboarderRole,
            EchoDTO.ConnectionStatus expectedConnectionStatus,
            EchoDTO.MTLSStatus expectedMTLSStatus,
            Boolean shouldCallAuthority) {
        // Given

        var identityAttributes = List.of("CodeIA_004", "CodeIA_005");
        EchoDTO expectedEcho = an(EchoDTO.class);
        expectedEcho.setUserIdentityAttributes(identityAttributes);

        if (shouldCallAuthority) {
            given(authorityExchange.echo()).willReturn(expectedEcho.getParticipant());
        }

        given(jwtService.getIdentityAttributes()).willReturn(identityAttributes);
        given(jwtService.getEmail()).willReturn(expectedEcho.getEmail());
        given(jwtService.getUsername()).willReturn(expectedEcho.getUsername());
        given(credentialService.hasCredential()).willReturn(hasCredential);
        expectedEcho.setConnectionStatus(expectedConnectionStatus);
        expectedEcho.setMtlsStatus(expectedMTLSStatus);

        if (shouldCallAuthority) given(jwtService.hasRole(Roles.ONBOARDER_M)).willReturn(hasOnboarderRole);

        if (!hasOnboarderRole || !shouldCallAuthority) expectedEcho.setParticipant(null);

        // When
        var actualEcho = agentService.echo();

        // Then
        if (shouldCallAuthority) {
            then(authorityExchange).should(times(1)).echo();
        }

        assertThat(actualEcho).isEqualTo(expectedEcho);
    }

    @Test
    void ping_withFqdn_shouldSucceed() {
        var expectedIdentityAttributesElement = a(IdentityAttributeDTO.class);
        var expectedIdentityAttributes = List.of(expectedIdentityAttributesElement);
        testPing("some.fqdn.com", expectedIdentityAttributes, false);
    }

    @Test
    void ping_withUrlWithHttpsScheme_shouldSucceed() {
        var expectedIdentityAttributesElement = a(IdentityAttributeDTO.class);
        var expectedIdentityAttributes = List.of(expectedIdentityAttributesElement);
        testPing(
                UriComponentsBuilder.fromUri(anURI()).scheme("https").toUriString(), expectedIdentityAttributes, false);
    }

    @Test
    void ping_withUrlWithoutHttpsScheme_shouldThrowInvalidFqdnException() {
        var expectedIdentityAttributesElement = a(IdentityAttributeDTO.class);
        var expectedIdentityAttributes = List.of(expectedIdentityAttributesElement);

        var exception = catchException(() -> testPing(
                UriComponentsBuilder.fromUri(anURI()).scheme("http").toUriString(), expectedIdentityAttributes, true));
        assertThat(exception).isInstanceOf(InvalidFqdnException.class);
    }

    void testPing(String fqdn, List<IdentityAttributeDTO> expectedIdentityAttributes, boolean shouldThrow) {

        // Given

        if (!shouldThrow) {
            given(mtlsClientBuilder
                            .buildParticipantClient(argThat(uri -> uri.endsWith(fqdn)))
                            .ping())
                    .willReturn(new ParticipantWithIdentityAttributesDTO()
                            .setIdentityAttributes(expectedIdentityAttributes));
        }

        // When

        var actualIdentityAttributes = agentService.ping(fqdn).getIdentityAttributes();

        // Then

        assertThat(actualIdentityAttributes).isEqualTo(expectedIdentityAttributes);
    }

    @Test
    void requestEphemeralProofFromAuthority_whenIsCached_AuthorityIsNotCalled() {
        var junitProof = "junit-prooof";
        given(ephemeralProofAdapter.loadEphemeralProof()).willReturn(Optional.of(junitProof));

        var result = agentService.requestEphemeralProofFromAuthority();

        assertThat(result).as("Ephemeral proof returned").isEqualTo(junitProof);
        verify(authorityExchange, never()).token();
        verify(ephemeralProofAdapter, never()).storeEphemeralProof(junitProof);
    }

    @Test
    void requestEphemeralProofFromAuthority_whenValidAuthorityToken_storeEphemeralProf() {
        var junitProof = "junit-prooof";
        given(ephemeralProofAdapter.loadEphemeralProof()).willReturn(Optional.empty());
        given(authorityExchange.token()).willReturn(junitProof);

        var result = agentService.requestEphemeralProofFromAuthority();

        assertThat(result).as("Ephemeral proof returned").isEqualTo(junitProof);
        verify(ephemeralProofAdapter).storeEphemeralProof(junitProof);
    }
}
