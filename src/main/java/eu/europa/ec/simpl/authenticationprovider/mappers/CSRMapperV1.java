package eu.europa.ec.simpl.authenticationprovider.mappers;

import eu.europa.ec.simpl.common.model.dto.authenticationprovider.DistinguishedNameDTO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR, unmappedSourcePolicy = ReportingPolicy.ERROR)
public interface CSRMapperV1 {
    DistinguishedNameDTO toV0(
            eu.europa.ec.simpl.api.authenticationprovider.v1.model.DistinguishedNameDTO distinguishedNameDTO);
}
