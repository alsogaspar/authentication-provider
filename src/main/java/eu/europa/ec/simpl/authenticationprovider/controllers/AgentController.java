package eu.europa.ec.simpl.authenticationprovider.controllers;

import eu.europa.ec.simpl.authenticationprovider.services.AgentService;
import eu.europa.ec.simpl.authenticationprovider.services.CredentialId;
import eu.europa.ec.simpl.common.exchanges.authenticationprovider.AgentControllerExchange;
import eu.europa.ec.simpl.common.model.dto.authenticationprovider.EchoDTO;
import eu.europa.ec.simpl.common.model.dto.identityprovider.ParticipantWithIdentityAttributesDTO;
import eu.europa.ec.simpl.common.model.dto.securityattributesprovider.IdentityAttributeDTO;
import eu.europa.ec.simpl.common.model.dto.securityattributesprovider.IdentityAttributeWithOwnershipDTO;
import java.util.List;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AgentController implements AgentControllerExchange {
    private final AgentService agentService;

    public AgentController(AgentService agentService) {
        this.agentService = agentService;
    }

    @Override
    public EchoDTO echo() {
        return agentService.echo();
    }

    @Override
    public List<IdentityAttributeWithOwnershipDTO> getIdentityAttributesWithOwnership() {
        return agentService.getAndSyncIdentityAttributes();
    }

    @Override
    public List<IdentityAttributeDTO> getParticipantIdentityAttributes(@PathVariable String credentialId) {
        return agentService.getParticipantIdentityAttributes(CredentialId.decode(credentialId));
    }

    @Override
    public ParticipantWithIdentityAttributesDTO ping(@RequestParam String fqdn) {
        return agentService.ping(fqdn);
    }
}
