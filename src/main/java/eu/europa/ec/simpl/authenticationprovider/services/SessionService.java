package eu.europa.ec.simpl.authenticationprovider.services;

import eu.europa.ec.simpl.common.model.dto.authenticationprovider.TierOneSessionDTO;
import eu.europa.ec.simpl.common.model.dto.securityattributesprovider.IdentityAttributeDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.springframework.validation.annotation.Validated;

@Validated
public interface SessionService {
    List<IdentityAttributeDTO> getIdentityAttributesOfParticipant(CredentialId credentialId);

    List<IdentityAttributeDTO> getIdentityAttributesOfParticipant(@NotNull String credentialId);

    void validateTierOneSession(@Valid TierOneSessionDTO session);
}
