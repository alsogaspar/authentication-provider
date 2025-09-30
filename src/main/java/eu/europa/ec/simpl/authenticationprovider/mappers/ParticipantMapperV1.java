package eu.europa.ec.simpl.authenticationprovider.mappers;

import eu.europa.ec.simpl.api.identityprovider.v1.model.ParticipantDTO;
import org.mapstruct.Mapper;

@Mapper
public interface ParticipantMapperV1 {
    ParticipantDTO toV1(eu.europa.ec.simpl.common.model.dto.identityprovider.ParticipantDTO dtoV0);
}
