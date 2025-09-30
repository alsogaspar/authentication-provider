package eu.europa.ec.simpl.authenticationprovider.controllers;

import static eu.europa.ec.simpl.common.test.TestUtil.a;
import static eu.europa.ec.simpl.common.test.TestUtil.an;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import eu.europa.ec.simpl.api.authenticationprovider.v1.model.TierOneSessionDTO;
import eu.europa.ec.simpl.authenticationprovider.mappers.IdentityAttributeMapperV1Impl;
import eu.europa.ec.simpl.authenticationprovider.utils.DtoUtils;
import eu.europa.ec.simpl.common.model.dto.securityattributesprovider.IdentityAttributeDTO;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@Import({
    SessionControllerV1.class,
    IdentityAttributeMapperV1Impl.class,
})
public class SessionControllerV1Test {

    @MockitoBean
    private SessionController controller;

    @Autowired
    private SessionControllerV1 controllerV1;

    @Test
    void getIdentityAttributesOfParticipantTest() {
        var credentialId = "junit-credentialId";
        var resultElementC0 = an(IdentityAttributeDTO.class);
        var resultC0 = List.of(resultElementC0);
        given(controller.getIdentityAttributesOfParticipant(credentialId)).willReturn(resultC0);
        var result = controllerV1.getIdentityAttributesOfParticipant(credentialId);

        assertThat(DtoUtils.areJsonEquals(result, resultC0));
    }

    @Test
    void validateTierOneSessionTest() {
        var tierOneSessionDTO = a(TierOneSessionDTO.class);
        controllerV1.validateTierOneSession(tierOneSessionDTO);
        verify(controller)
                .validateTierOneSession(argThat(session -> DtoUtils.areJsonEquals(session, tierOneSessionDTO)));
    }
}
