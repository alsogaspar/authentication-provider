package eu.europa.ec.simpl.authenticationprovider.mappers;

import eu.europa.ec.simpl.api.authenticationprovider.v1.model.IdentityAttributeDTO;
import eu.europa.ec.simpl.api.authenticationprovider.v1.model.PageMetadataDTO;
import eu.europa.ec.simpl.api.authenticationprovider.v1.model.PagedModelIdentityAttributeWithOwnershipDTO;
import eu.europa.ec.simpl.api.authenticationprovider.v1.model.ParticipantWithIdentityAttributesDTO;
import eu.europa.ec.simpl.api.authenticationprovider.v1.model.SearchIdentityAttributesWithOwnershipFilterParameterDTO;
import eu.europa.ec.simpl.authenticationprovider.filters.IdentityAttributeWithOwnershipFilter;
import eu.europa.ec.simpl.common.model.dto.authenticationprovider.TierOneSessionDTO;
import eu.europa.ec.simpl.common.model.dto.securityattributesprovider.IdentityAttributeWithOwnershipDTO;
import java.util.List;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.data.domain.Page;

@Mapper(unmappedSourcePolicy = ReportingPolicy.ERROR, unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface IdentityAttributeMapperV1 {
    IdentityAttributeWithOwnershipFilter toV0(SearchIdentityAttributesWithOwnershipFilterParameterDTO filter);

    @Mapping(target = "page", source = ".")
    @Mapping(target = "content", source = "content")
    PagedModelIdentityAttributeWithOwnershipDTO toV1(Page<IdentityAttributeWithOwnershipDTO> search);

    @BeanMapping(
            ignoreUnmappedSourceProperties = {
                "empty",
                "numberOfElements",
                "content",
                "sort",
                "first",
                "last",
                "pageable"
            })
    PageMetadataDTO toPageMetadataDTO(Page<IdentityAttributeWithOwnershipDTO> page);

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

    List<IdentityAttributeDTO> toListIdentityAttributeDTOV1(
            List<eu.europa.ec.simpl.common.model.dto.securityattributesprovider.IdentityAttributeDTO>
                    identityAttributesOfParticipant);

    TierOneSessionDTO toV0(eu.europa.ec.simpl.api.authenticationprovider.v1.model.TierOneSessionDTO tierOneSessionDTO);

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
    eu.europa.ec.simpl.api.authenticationprovider.v1.model.IdentityAttributeWithOwnershipDTO toV1(
            IdentityAttributeWithOwnershipDTO identityAttributeWithOwnershipDTO);
}
