package eu.europa.ec.simpl.authenticationprovider.mappers;

import static eu.europa.ec.simpl.common.test.TestUtil.a;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import eu.europa.ec.simpl.api.authenticationprovider.v1.model.SearchIdentityAttributesWithOwnershipFilterParameterDTO;
import eu.europa.ec.simpl.common.model.dto.securityattributesprovider.IdentityAttributeWithOwnershipDTO;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;

public class IdentityAttributeMapperV1Test {
    private final IdentityAttributeMapperV1Impl identityAttributeMapper = new IdentityAttributeMapperV1Impl();

    @Test
    void toV0Test() {
        assertDoesNotThrow(
                () -> identityAttributeMapper.toV0(a(SearchIdentityAttributesWithOwnershipFilterParameterDTO.class)));
        assertDoesNotThrow(() -> identityAttributeMapper.toV0(
                a(eu.europa.ec.simpl.api.authenticationprovider.v1.model.TierOneSessionDTO.class)));
    }

    @Test
    void toV1Test() {
        assertDoesNotThrow(() ->
                identityAttributeMapper.toV1(new PageImpl<>(List.of(a(IdentityAttributeWithOwnershipDTO.class)))));
        assertDoesNotThrow(() -> identityAttributeMapper.toV1(
                a(eu.europa.ec.simpl.common.model.dto.identityprovider.ParticipantWithIdentityAttributesDTO.class)));
        assertDoesNotThrow(() -> identityAttributeMapper.toV1(a(IdentityAttributeWithOwnershipDTO.class)));
    }

    @Test
    void toPageMetadataDTO() {
        assertDoesNotThrow(() -> identityAttributeMapper.toPageMetadataDTO(
                new PageImpl<>(List.of(a(IdentityAttributeWithOwnershipDTO.class)))));
    }

    @Test
    void toListIdentityAttributeDTOV1Test() {
        assertDoesNotThrow(() -> identityAttributeMapper.toListIdentityAttributeDTOV1(
                List.of(a(eu.europa.ec.simpl.common.model.dto.securityattributesprovider.IdentityAttributeDTO.class))));
    }
}
