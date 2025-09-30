package eu.europa.ec.simpl.authenticationprovider.controllers;

import eu.europa.ec.simpl.api.authenticationprovider.v1.exchanges.AgentsApi;
import eu.europa.ec.simpl.api.authenticationprovider.v1.model.EchoDTO;
import eu.europa.ec.simpl.api.authenticationprovider.v1.model.IdentityAttributeDTO;
import eu.europa.ec.simpl.api.authenticationprovider.v1.model.IdentityAttributeWithOwnershipDTO;
import eu.europa.ec.simpl.api.authenticationprovider.v1.model.ParticipantWithIdentityAttributesDTO;
import eu.europa.ec.simpl.authenticationprovider.mappers.AgentMapperV1;
import eu.europa.ec.simpl.authenticationprovider.services.AgentService;
import eu.europa.ec.simpl.authenticationprovider.services.CredentialService;
import java.util.List;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1")
public class AgentControllerV1 implements AgentsApi {

    private final AgentController controller;
    private final CredentialService credentialService;
    private final AgentService agentService;
    private final AgentMapperV1 mapper;

    public AgentControllerV1(
            AgentController controller,
            CredentialService credentialService,
            AgentMapperV1 mapper,
            AgentService agentService) {
        this.controller = controller;
        this.credentialService = credentialService;
        this.mapper = mapper;
        this.agentService = agentService;
    }

    @Override
    public EchoDTO echo() {
        return mapper.toV1(controller.echo());
    }

    @Override
    public List<IdentityAttributeWithOwnershipDTO> getIdentityAttributesWithOwnership() {
        return mapper.toListIdentityAttributeWithOwnershipDTOV1(controller.getIdentityAttributesWithOwnership());
    }

    @Override
    public List<IdentityAttributeDTO> getParticipantIdentityAttributes(String credentialId) {
        return mapper.toListIdentityAttributeDTOV1(controller.getParticipantIdentityAttributes(credentialId));
    }

    @Override
    public ParticipantWithIdentityAttributesDTO pingAgent(String fqdn) {
        return mapper.toV1(controller.ping(fqdn));
    }

    @Override
    public String requestEphemeralProofFromAuthority() {
        return agentService.requestEphemeralProofFromAuthority();
    }

    @Override
    public void validateCredential(String body) {
        credentialService.validateCredential(body);
    }
}
