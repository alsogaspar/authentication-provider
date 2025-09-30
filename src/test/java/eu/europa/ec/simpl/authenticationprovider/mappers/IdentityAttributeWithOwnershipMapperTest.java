package eu.europa.ec.simpl.authenticationprovider.mappers;

import static eu.europa.ec.simpl.common.test.TestUtil.a;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import eu.europa.ec.simpl.authenticationprovider.entities.IdentityAttributeWithOwnership;
import eu.europa.ec.simpl.common.model.dto.securityattributesprovider.IdentityAttributeDTO;
import eu.europa.ec.simpl.common.model.dto.securityattributesprovider.IdentityAttributeWithOwnershipDTO;
import org.junit.jupiter.api.Test;

public class IdentityAttributeWithOwnershipMapperTest {
    private final IdentityAttributeWithOwnershipMapperImpl identityAttributeWithOwnershipMapper =
            new IdentityAttributeWithOwnershipMapperImpl();

    @Test
    void toEntityTest() {
        assertDoesNotThrow(
                () -> identityAttributeWithOwnershipMapper.toEntity(a(IdentityAttributeDTO.class), a(Boolean.class)));
        assertDoesNotThrow(
                () -> identityAttributeWithOwnershipMapper.toEntity(a(IdentityAttributeWithOwnershipDTO.class)));
    }

    @Test
    void toLightDtoWithOwnershipTest() {
        assertDoesNotThrow(() ->
                identityAttributeWithOwnershipMapper.toLightDtoWithOwnership(a(IdentityAttributeWithOwnership.class)));
    }

    @Test
    void toLightDtoTest() {
        assertDoesNotThrow(
                () -> identityAttributeWithOwnershipMapper.toLightDto(a(IdentityAttributeWithOwnership.class)));
    }

    @Test
    void updateIdentityAttributeTest() {
        assertDoesNotThrow(() -> identityAttributeWithOwnershipMapper.updateIdentityAttribute(
                a(IdentityAttributeWithOwnership.class), a(IdentityAttributeDTO.class)));
    }
}
