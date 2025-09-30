package eu.europa.ec.simpl.authenticationprovider.mappers;

import static eu.europa.ec.simpl.common.test.TestUtil.a;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

public class CSRMapperV1Test {
    private final CSRMapperV1Impl cSRMapper = new CSRMapperV1Impl();

    @Test
    void toV0Test() {
        assertDoesNotThrow(() ->
                cSRMapper.toV0(a(eu.europa.ec.simpl.api.authenticationprovider.v1.model.DistinguishedNameDTO.class)));
    }
}
