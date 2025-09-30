package eu.europa.ec.simpl.authenticationprovider.mappers;

import static eu.europa.ec.simpl.common.test.TestUtil.a;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.List;
import org.junit.jupiter.api.Test;

public class AgentMapperV1Test {
    private final AgentMapperV1Impl agentMapper = new AgentMapperV1Impl();

    @Test
    void toV1Test() {
        assertDoesNotThrow(
                () -> agentMapper.toV1(a(eu.europa.ec.simpl.common.model.dto.authenticationprovider.EchoDTO.class)));
        assertDoesNotThrow(() -> agentMapper.toV1(
                a(eu.europa.ec.simpl.common.model.dto.identityprovider.ParticipantWithIdentityAttributesDTO.class)));
        assertDoesNotThrow(() -> agentMapper.toV1(a(
                eu.europa.ec.simpl.common.model.dto.securityattributesprovider.IdentityAttributeWithOwnershipDTO
                        .class)));
    }

    @Test
    void toListIdentityAttributeWithOwnershipDTOV1Test() {
        assertDoesNotThrow(() -> agentMapper.toListIdentityAttributeWithOwnershipDTOV1(List.of(a(
                eu.europa.ec.simpl.common.model.dto.securityattributesprovider.IdentityAttributeWithOwnershipDTO
                        .class))));
    }

    @Test
    void toListIdentityAttributeDTOV1Test() {
        assertDoesNotThrow(() -> agentMapper.toListIdentityAttributeDTOV1(
                List.of(a(eu.europa.ec.simpl.common.model.dto.securityattributesprovider.IdentityAttributeDTO.class))));
    }
}
