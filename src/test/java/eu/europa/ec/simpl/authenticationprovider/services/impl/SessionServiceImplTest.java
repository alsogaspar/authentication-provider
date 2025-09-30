package eu.europa.ec.simpl.authenticationprovider.services.impl;

import static eu.europa.ec.simpl.common.test.MockUtil.spyLambda;
import static eu.europa.ec.simpl.common.test.TestUtil.an;
import static eu.europa.ec.simpl.common.test.TestUtil.anHash;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;

import eu.europa.ec.simpl.authenticationprovider.repositories.EphemeralProofRepository;
import eu.europa.ec.simpl.authenticationprovider.services.CredentialId;
import eu.europa.ec.simpl.common.ephemeralproof.JwtEphemeralProofParser;
import eu.europa.ec.simpl.common.model.dto.securityattributesprovider.IdentityAttributeDTO;
import eu.europa.ec.simpl.common.redis.entity.EphemeralProof;
import java.util.List;
import java.util.function.Function;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SessionServiceImplTest {

    @Mock
    EphemeralProofRepository ephemeralProofRepository;

    @Mock
    JwtEphemeralProofParser ephemeralProofParser;

    Function<String, JwtEphemeralProofParser> ephemeralProofParserFactory = spyLambda(s -> ephemeralProofParser);

    @InjectMocks
    SessionServiceImpl sessionService;

    @Test
    void getIdentityAttributesOfParticipant() {
        var ephemeralProof = an(EphemeralProof.class);
        var expectedIdentityAttributesElement = an(IdentityAttributeDTO.class);
        var expectedIdentityAttributes = List.of(expectedIdentityAttributesElement);

        // Given
        given(ephemeralProofRepository.findByIdOrThrow(any())).willReturn(ephemeralProof);
        given(ephemeralProofParser.getIdentityAttributes()).willReturn(expectedIdentityAttributes);
        // When
        var actualIdentityAttributes = sessionService.getIdentityAttributesOfParticipant(CredentialId.decode(anHash()));
        // Then
        assertThat(actualIdentityAttributes).isEqualTo(expectedIdentityAttributes);
    }
}
