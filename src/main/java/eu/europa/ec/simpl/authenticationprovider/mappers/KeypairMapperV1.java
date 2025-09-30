package eu.europa.ec.simpl.authenticationprovider.mappers;

import eu.europa.ec.simpl.api.authenticationprovider.v1.model.KeyPairDTO;
import eu.europa.ec.simpl.common.model.dto.authenticationprovider.ImportKeyPairDTO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR, unmappedSourcePolicy = ReportingPolicy.ERROR)
public interface KeypairMapperV1 {

    KeyPairDTO toV1(eu.europa.ec.simpl.common.model.dto.authenticationprovider.KeyPairDTO installedKeyPair);

    ImportKeyPairDTO toV0(eu.europa.ec.simpl.api.authenticationprovider.v1.model.ImportKeyPairDTO importKeyPairDTO);
}
