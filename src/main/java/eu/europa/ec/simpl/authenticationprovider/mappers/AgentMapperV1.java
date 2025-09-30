package eu.europa.ec.simpl.authenticationprovider.mappers;

import eu.europa.ec.simpl.api.authenticationprovider.v1.model.EchoDTO;
import eu.europa.ec.simpl.api.authenticationprovider.v1.model.IdentityAttributeDTO;
import eu.europa.ec.simpl.api.authenticationprovider.v1.model.IdentityAttributeWithOwnershipDTO;
import eu.europa.ec.simpl.api.authenticationprovider.v1.model.ParticipantWithIdentityAttributesDTO;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedSourcePolicy = ReportingPolicy.ERROR, unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface AgentMapperV1 {

    @Mapping(target = "id", source = "participant.participant.id")
    @Mapping(target = "participantType", source = "participant.participant.participantType")
    @Mapping(target = "organization", source = "participant.participant.organization")
    @Mapping(target = "creationTimestamp", source = "participant.participant.creationTimestamp")
    @Mapping(target = "updateTimestamp", source = "participant.participant.updateTimestamp")
    @Mapping(target = "credentialId", source = "participant.participant.credentialId")
    @Mapping(target = "expiryDate", source = "participant.participant.expiryDate")
    @Mapping(target = "identityAttributes", source = "participant.identityAttributes")
    EchoDTO toV1(eu.europa.ec.simpl.common.model.dto.authenticationprovider.EchoDTO echo);

    List<IdentityAttributeWithOwnershipDTO> toListIdentityAttributeWithOwnershipDTOV1(
            List<eu.europa.ec.simpl.common.model.dto.securityattributesprovider.IdentityAttributeWithOwnershipDTO>
                    identityAttributesWithOwnership);

    List<IdentityAttributeDTO> toListIdentityAttributeDTOV1(
            List<eu.europa.ec.simpl.common.model.dto.securityattributesprovider.IdentityAttributeDTO>
                    participantIdentityAttributes);

    @Mapping(target = "id", source = "participant.id")
    @Mapping(target = "participantType", source = "participant.participantType")
    @Mapping(target = "organization", source = "participant.organization")
    @Mapping(target = "creationTimestamp", source = "participant.creationTimestamp")
    @Mapping(target = "updateTimestamp", source = "participant.updateTimestamp")
    @Mapping(target = "credentialId", source = "participant.credentialId")
    @Mapping(target = "expiryDate", source = "participant.expiryDate")
    @Mapping(target = "identityAttributes", source = "identityAttributes")
    ParticipantWithIdentityAttributesDTO toV1(
            eu.europa.ec.simpl.common.model.dto.identityprovider.ParticipantWithIdentityAttributesDTO ping);

    @Mapping(target = "id", source = "identityAttribute.id")
    @Mapping(target = "code", source = "identityAttribute.code")
    @Mapping(target = "name", source = "identityAttribute.name")
    @Mapping(target = "description", source = "identityAttribute.description")
    @Mapping(target = "assignableToRoles", source = "identityAttribute.assignableToRoles")
    @Mapping(target = "enabled", source = "identityAttribute.enabled")
    @Mapping(target = "creationTimestamp", source = "identityAttribute.creationTimestamp")
    @Mapping(target = "updateTimestamp", source = "identityAttribute.updateTimestamp")
    @Mapping(target = "participantTypes", source = "identityAttribute.participantTypes")
    @Mapping(target = "used", source = "identityAttribute.used")
    IdentityAttributeWithOwnershipDTO toV1(
            eu.europa.ec.simpl.common.model.dto.securityattributesprovider.IdentityAttributeWithOwnershipDTO
                    identityAttributeWithOwnershipDTO);
}
