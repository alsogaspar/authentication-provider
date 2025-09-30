package eu.europa.ec.simpl.authenticationprovider.controllers;

import eu.europa.ec.simpl.authenticationprovider.services.CredentialService;
import eu.europa.ec.simpl.common.exchanges.authenticationprovider.CredentialExchange;
import eu.europa.ec.simpl.common.model.dto.authenticationprovider.CredentialDTO;
import eu.europa.ec.simpl.common.model.dto.identityprovider.ParticipantDTO;
import lombok.SneakyThrows;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class CredentialController implements CredentialExchange {

    private final CredentialService service;

    public CredentialController(CredentialService service) {
        this.service = service;
    }

    @Override
    @SneakyThrows
    public long uploadCredential(MultipartFile file) {
        return service.insert(file.getBytes());
    }

    @Override
    public boolean hasCredential() {
        return service.hasCredential();
    }

    @Override
    public void delete() {
        service.deleteCredential();
    }

    @Override
    public Resource downloadInstalledCredentials() {
        return new ByteArrayResource(service.getCredential());
    }

    @Override
    public CredentialDTO getPublicKey() {
        return service.getPublicKey();
    }

    @Override
    public ParticipantDTO getMyParticipantId() {
        return service.getMyParticipantId();
    }

    @Override
    public ParticipantDTO getCredentialId() {
        return new ParticipantDTO().setCredentialId(service.getCredentialId());
    }
}
