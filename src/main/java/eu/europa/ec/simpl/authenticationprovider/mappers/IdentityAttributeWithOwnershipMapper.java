package eu.europa.ec.simpl.authenticationprovider.mappers;

import eu.europa.ec.simpl.authenticationprovider.entities.IdentityAttributeWithOwnership;
import eu.europa.ec.simpl.common.model.dto.securityattributesprovider.IdentityAttributeDTO;
import eu.europa.ec.simpl.common.model.dto.securityattributesprovider.IdentityAttributeWithOwnershipDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper
public interface IdentityAttributeWithOwnershipMapper {

    IdentityAttributeWithOwnership toEntity(IdentityAttributeDTO dto, Boolean assignedToParticipant);

    @Mapping(target = ".", source = "identityAttribute")
    IdentityAttributeWithOwnership toEntity(IdentityAttributeWithOwnershipDTO dto);

    @Mapping(target = "identityAttribute", source = ".")
    IdentityAttributeWithOwnershipDTO toLightDtoWithOwnership(IdentityAttributeWithOwnership entity);

    @Mapping(target = "participantTypes", ignore = true)
    IdentityAttributeDTO toLightDto(IdentityAttributeWithOwnership entity);

    void updateIdentityAttribute(@MappingTarget IdentityAttributeWithOwnership entity, IdentityAttributeDTO ida);
}
