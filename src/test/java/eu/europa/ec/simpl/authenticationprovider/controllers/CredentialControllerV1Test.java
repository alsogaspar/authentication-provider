package eu.europa.ec.simpl.authenticationprovider.controllers;

import static eu.europa.ec.simpl.common.test.TestUtil.a;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import eu.europa.ec.simpl.authenticationprovider.mappers.CredentialMapperV1Impl;
import eu.europa.ec.simpl.authenticationprovider.services.CredentialService;
import eu.europa.ec.simpl.authenticationprovider.utils.DtoUtils;
import eu.europa.ec.simpl.common.model.dto.authenticationprovider.CredentialDTO;
import eu.europa.ec.simpl.common.model.dto.identityprovider.ParticipantDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.Resource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(SpringExtension.class)
@Import({
    CredentialControllerV1.class,
    CredentialMapperV1Impl.class,
})
public class CredentialControllerV1Test {
    @MockitoBean
    private CredentialService credentialService;

    @MockitoBean
    private CredentialController controller;

    @Autowired
    private CredentialControllerV1 controllerV1;

    @Test
    void deleteTest() {
        controllerV1.delete();
        verify(controller).delete();
    }

    @Test
    void downloadInstalledCredentialsTest() {
        var resultC0 = mock(Resource.class);
        given(controller.downloadInstalledCredentials()).willReturn(resultC0);
        var resutl = controllerV1.downloadInstalledCredentials();

        verify(controller).downloadInstalledCredentials();

        assertThat(resutl).isEqualTo(resultC0);
    }

    @Test
    void getCredentialIdTest() {
        var resultC0 = a(ParticipantDTO.class);
        given(controller.getCredentialId()).willReturn(resultC0);

        var result = controllerV1.getCredentialId();

        assertThat(DtoUtils.areJsonEquals(result, resultC0)).isTrue();
    }

    @Test
    void getMyParticipantIdTest() {
        var resultC0 = a(ParticipantDTO.class);
        given(controller.getMyParticipantId()).willReturn(resultC0);

        var result = controllerV1.getMyParticipantId();

        assertThat(DtoUtils.areJsonEquals(result, resultC0)).isTrue();
    }

    @Test
    void getPublicKeyTest() {
        var resultC0 = a(CredentialDTO.class);
        given(controller.getPublicKey()).willReturn(resultC0);

        var result = controllerV1.getPublicKey();

        assertThat(result.getPublicKey()).isEqualTo(resultC0.getPublicKey());
    }

    @Test
    void uploadCredentialTest() {
        var file = mock(MultipartFile.class);
        var resultC0 = 3L;
        given(controller.uploadCredential(file)).willReturn(resultC0);

        var result = controllerV1.uploadCredential(file);

        assertThat(result).isEqualTo(resultC0);
    }
}
