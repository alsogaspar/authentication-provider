package eu.europa.ec.simpl.authenticationprovider.controllers;

import static eu.europa.ec.simpl.common.test.TestUtil.a;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import eu.europa.ec.simpl.api.authenticationprovider.v1.model.DistinguishedNameDTO;
import eu.europa.ec.simpl.authenticationprovider.mappers.CSRMapperV1Impl;
import eu.europa.ec.simpl.authenticationprovider.utils.DtoUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.Resource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@Import({
    CSRControllerV1.class,
    CSRMapperV1Impl.class,
})
public class CSRControllerV1Test {
    @MockitoBean
    private CSRController controller;

    @Autowired
    private CSRControllerV1 controllerV1;

    @Test
    void generateCSRTest() {
        var distinguishedNameDTO = a(DistinguishedNameDTO.class);
        var resultC0 = mock(Resource.class);
        given(controller.generateCSR(argThat(dto -> DtoUtils.areJsonEquals(distinguishedNameDTO, dto))))
                .willReturn(resultC0);
        var resutl = controllerV1.generateCSR(distinguishedNameDTO);
        assertThat(resutl).isEqualTo(resultC0);
    }
}
