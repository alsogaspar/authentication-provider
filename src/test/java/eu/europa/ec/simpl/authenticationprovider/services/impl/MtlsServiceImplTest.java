package eu.europa.ec.simpl.authenticationprovider.services.impl;

import static eu.europa.ec.simpl.common.test.TestUtil.a;
import static eu.europa.ec.simpl.common.test.TestUtil.an;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import eu.europa.ec.simpl.authenticationprovider.services.SessionService;
import eu.europa.ec.simpl.common.model.dto.securityattributesprovider.IdentityAttributeDTO;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MtlsServiceImplTest {

    @Mock
    SessionService sessionService;

    @InjectMocks
    MtlsServiceImpl mtlsService;

    @Test
    void ping() {
        var credentialId = a(String.class);
        var expectedIdentityAttributesElement = an(IdentityAttributeDTO.class);
        var expectedIdentityAttributes = List.of(expectedIdentityAttributesElement);

        // Given
        given(sessionService.getIdentityAttributesOfParticipant(credentialId)).willReturn(expectedIdentityAttributes);
        // When
        var actualIdentityAttributes = mtlsService.ping(credentialId).getIdentityAttributes();
        // Then
        assertThat(actualIdentityAttributes).isEqualTo(expectedIdentityAttributes);
    }
}
