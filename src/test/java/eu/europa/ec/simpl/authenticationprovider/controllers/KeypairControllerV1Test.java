package eu.europa.ec.simpl.authenticationprovider.controllers;

import static eu.europa.ec.simpl.common.test.TestUtil.a;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import eu.europa.ec.simpl.api.authenticationprovider.v1.model.ImportKeyPairDTO;
import eu.europa.ec.simpl.authenticationprovider.mappers.KeypairMapperV1Impl;
import eu.europa.ec.simpl.authenticationprovider.utils.DtoUtils;
import eu.europa.ec.simpl.common.model.dto.authenticationprovider.KeyPairDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@Import({
    KeypairControllerV1.class,
    KeypairMapperV1Impl.class,
})
public class KeypairControllerV1Test {

    @MockitoBean
    private KeypairController controller;

    @Autowired
    private KeypairControllerV1 controllerV1;

    @Test
    void existsKeypairTest() {
        controllerV1.existsKeypair();
        verify(controller).existsKeypair();
    }

    @Test
    void generateKeyPairTest() {
        controllerV1.generateKeyPair();
        verify(controller).generateKeyPair();
    }

    @Test
    void getInstalledKeyPairTest() {
        var resultC0 = a(KeyPairDTO.class);
        given(controller.getInstalledKeyPair()).willReturn(resultC0);
        var result = controllerV1.getInstalledKeyPair();
        assertThat(DtoUtils.areJsonEquals(result, resultC0)).isTrue();
    }

    @Test
    void importKeyPairTest() {
        var importKeyPairDTO = a(ImportKeyPairDTO.class);
        controllerV1.importKeyPair(importKeyPairDTO);
        verify(controller).importKeyPair(argThat(dto -> DtoUtils.areJsonEquals(dto, importKeyPairDTO)));
    }
}
