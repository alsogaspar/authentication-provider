package eu.europa.ec.simpl.authenticationprovider.services;

import eu.europa.ec.simpl.common.model.dto.authenticationprovider.EchoDTO;
import eu.europa.ec.simpl.common.model.dto.identityprovider.ParticipantWithIdentityAttributesDTO;
import eu.europa.ec.simpl.common.model.dto.securityattributesprovider.IdentityAttributeDTO;
import eu.europa.ec.simpl.common.model.dto.securityattributesprovider.IdentityAttributeWithOwnershipDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public interface AgentService {
    List<IdentityAttributeWithOwnershipDTO> getAndSyncIdentityAttributes();

    List<IdentityAttributeDTO> getParticipantIdentityAttributes(@NotNull @Valid CredentialId credentialId);

    EchoDTO echo();

    ParticipantWithIdentityAttributesDTO ping(String fqdn);

    String requestEphemeralProofFromAuthority();
}
