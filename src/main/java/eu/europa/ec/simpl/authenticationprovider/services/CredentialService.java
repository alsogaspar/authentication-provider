package eu.europa.ec.simpl.authenticationprovider.services;

import eu.europa.ec.simpl.common.model.dto.authenticationprovider.CredentialDTO;
import eu.europa.ec.simpl.common.model.dto.identityprovider.ParticipantDTO;
import org.springframework.transaction.annotation.Transactional;

public interface CredentialService {

    @Transactional
    long insert(byte[] fileContent);

    boolean hasCredential();

    byte[] getCredential();

    eu.europa.ec.simpl.api.authenticationprovider.v1.model.CredentialDTO getCredentialDTO();

    void deleteCredential();

    CredentialDTO getPublicKey();

    String getCredentialId();

    String getCredentialId(byte[] credentials);

    ParticipantDTO getMyParticipantId();

    void validateCredential(String body);
}
