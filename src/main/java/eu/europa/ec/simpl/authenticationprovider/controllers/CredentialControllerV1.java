package eu.europa.ec.simpl.authenticationprovider.controllers;

import eu.europa.ec.simpl.api.authenticationprovider.v1.exchanges.CredentialsApi;
import eu.europa.ec.simpl.api.authenticationprovider.v1.model.CredentialDTO;
import eu.europa.ec.simpl.api.authenticationprovider.v1.model.ParticipantDTO;
import eu.europa.ec.simpl.authenticationprovider.mappers.CredentialMapperV1;
import eu.europa.ec.simpl.authenticationprovider.services.CredentialService;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("v1")
public class CredentialControllerV1 implements CredentialsApi {

    private final CredentialService credentialService;

    private final CredentialController controller;
    private final CredentialMapperV1 mapper;

    public CredentialControllerV1(
            CredentialService credentialService, CredentialController controller, CredentialMapperV1 mapper) {
        this.credentialService = credentialService;
        this.controller = controller;
        this.mapper = mapper;
    }

    @Override
    public void delete() {
        controller.delete();
    }

    @Override
    public Resource downloadInstalledCredentials() {
        return controller.downloadInstalledCredentials();
    }

    @Override
    public CredentialDTO getCredential() {
        return credentialService.getCredentialDTO();
    }

    @Override
    public ParticipantDTO getCredentialId() {
        return mapper.toV1(controller.getCredentialId());
    }

    @Override
    public ParticipantDTO getMyParticipantId() {
        return mapper.toV1(controller.getMyParticipantId());
    }

    @Override
    public CredentialDTO getPublicKey() {
        return mapper.toV1(controller.getPublicKey());
    }

    @Override
    public Integer uploadCredential(MultipartFile file) {
        return ((Number) controller.uploadCredential(file)).intValue();
    }
}
