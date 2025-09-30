package eu.europa.ec.simpl.authenticationprovider.controllers;

import static eu.europa.ec.simpl.common.test.TestUtil.a;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import eu.europa.ec.simpl.authenticationprovider.mappers.AgentMapperV1Impl;
import eu.europa.ec.simpl.authenticationprovider.services.AgentService;
import eu.europa.ec.simpl.authenticationprovider.services.CredentialService;
import eu.europa.ec.simpl.authenticationprovider.utils.DtoUtils;
import eu.europa.ec.simpl.common.model.dto.authenticationprovider.EchoDTO;
import eu.europa.ec.simpl.common.model.dto.identityprovider.ParticipantWithIdentityAttributesDTO;
import eu.europa.ec.simpl.common.model.dto.securityattributesprovider.IdentityAttributeDTO;
import eu.europa.ec.simpl.common.model.dto.securityattributesprovider.IdentityAttributeWithOwnershipDTO;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AgentControllerV1Test {

    @Mock
    private AgentController controller;

    @Mock
    private CredentialService credentialService;

    @Mock
    private AgentService agentService;

    @Mock(answer = Answers.CALLS_REAL_METHODS)
    private AgentMapperV1Impl mapper;

    @InjectMocks
    private AgentControllerV1 controllerV1;

    @Test
    void echoTest() {
        var resultC0 = a(EchoDTO.class);
        given(controller.echo()).willReturn(resultC0);
        var result = controllerV1.echo();
        assertThat(DtoUtils.areJsonEquals(result, resultC0)).isTrue();
    }

    @Test
    void getIdentityAttributesWithOwnershipTest() {
        var resultC0 = List.of(a(IdentityAttributeWithOwnershipDTO.class));
        given(controller.getIdentityAttributesWithOwnership()).willReturn(resultC0);

        var result = controllerV1.getIdentityAttributesWithOwnership();

        assertThat(DtoUtils.areJsonEquals(result, resultC0)).isTrue();
    }

    @Test
    void getParticipantIdentityAttributesTest() {
        var resultC0 = List.of(a(IdentityAttributeDTO.class));
        var credentialId = "junit-credentialId";
        given(controller.getParticipantIdentityAttributes(credentialId)).willReturn(resultC0);

        var result = controllerV1.getParticipantIdentityAttributes(credentialId);

        assertThat(DtoUtils.areJsonEquals(result, resultC0)).isTrue();
    }

    @Test
    void pingAgentTest() {
        var fqdn = "junti-fqdn";
        var resultC0 = a(ParticipantWithIdentityAttributesDTO.class);
        given(controller.ping(fqdn)).willReturn(resultC0);
        var result = controllerV1.pingAgent(fqdn);
        assertThat(DtoUtils.areJsonEquals(result, resultC0)).isTrue();
    }
}
