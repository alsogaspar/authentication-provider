package eu.europa.ec.simpl.authenticationprovider.mappers;

import eu.europa.ec.simpl.api.authenticationprovider.v1.model.CredentialDTO;
import eu.europa.ec.simpl.api.authenticationprovider.v1.model.ParticipantDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedSourcePolicy = ReportingPolicy.ERROR, unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface CredentialMapperV1 {
    ParticipantDTO toV1(eu.europa.ec.simpl.common.model.dto.identityprovider.ParticipantDTO credentialId);

    @Mapping(target = "credentialId", ignore = true)
    @Mapping(target = "participantId", ignore = true)
    CredentialDTO toV1(eu.europa.ec.simpl.common.model.dto.authenticationprovider.CredentialDTO publicKey);
}
