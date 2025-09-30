package eu.europa.ec.simpl.authenticationprovider.services;

import eu.europa.ec.simpl.common.model.dto.identityprovider.ParticipantWithIdentityAttributesDTO;
import jakarta.validation.constraints.NotBlank;

public interface MtlsService {

    ParticipantWithIdentityAttributesDTO ping(String credentialId);

    void insertEphemeralProof(String credentialId, @NotBlank String ephemeralProof);
}
