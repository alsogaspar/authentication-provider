package eu.europa.ec.simpl.authenticationprovider.controllers;

import static eu.europa.ec.simpl.common.test.TestUtil.a;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import eu.europa.ec.simpl.authenticationprovider.mappers.IdentityAttributeMapperV1Impl;
import eu.europa.ec.simpl.authenticationprovider.services.MtlsService;
import eu.europa.ec.simpl.authenticationprovider.utils.DtoUtils;
import eu.europa.ec.simpl.common.model.dto.identityprovider.ParticipantWithIdentityAttributesDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@Import({
    MTLSControllerV1.class,
    IdentityAttributeMapperV1Impl.class,
})
public class MTLSControllerV1Test {
    @MockitoBean
    private MtlsService mtlsService;

    @MockitoBean
    private MTLSController controller;

    @Autowired
    private MTLSControllerV1 controllerV1;

    @Test
    void pingTest() {
        var credentialId = "junit-credentil-id";
        var resultC0 = a(ParticipantWithIdentityAttributesDTO.class);
        given(controller.ping(credentialId)).willReturn(resultC0);

        var result = controllerV1.ping(credentialId);

        assertThat(DtoUtils.areJsonEquals(resultC0, result)).isTrue();
    }

    @Test
    void storeCallerEphemeralProofTest() {
        String credentialId = "junit-credential-id";
        String ephemeralProof = "junit-ephemeral-proof";
        controllerV1.storeCallerEphemeralProof(credentialId, ephemeralProof);
        verify(controller).storeCallerEphemeralProof(credentialId, ephemeralProof);
    }
}
