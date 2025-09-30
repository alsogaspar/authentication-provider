package eu.europa.ec.simpl.authenticationprovider.mappers;

import static eu.europa.ec.simpl.common.test.TestUtil.a;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

public class CredentialMapperV1Test {
    private final CredentialMapperV1Impl credentialMapper = new CredentialMapperV1Impl();

    @Test
    void toV1Test() {
        assertDoesNotThrow(() ->
                credentialMapper.toV1(a(eu.europa.ec.simpl.common.model.dto.identityprovider.ParticipantDTO.class)));
        assertDoesNotThrow(() -> credentialMapper.toV1(
                a(eu.europa.ec.simpl.common.model.dto.authenticationprovider.CredentialDTO.class)));
    }
}
