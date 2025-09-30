package eu.europa.ec.simpl.authenticationprovider.controllers;

import eu.europa.ec.simpl.api.authenticationprovider.v1.exchanges.SessionsApi;
import eu.europa.ec.simpl.api.authenticationprovider.v1.model.IdentityAttributeDTO;
import eu.europa.ec.simpl.api.authenticationprovider.v1.model.TierOneSessionDTO;
import eu.europa.ec.simpl.authenticationprovider.mappers.IdentityAttributeMapperV1;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1")
public class SessionControllerV1 implements SessionsApi {

    private final SessionController controller;
    private final IdentityAttributeMapperV1 mapper;

    public SessionControllerV1(SessionController controller, IdentityAttributeMapperV1 mapper) {
        this.controller = controller;
        this.mapper = mapper;
    }

    @Override
    public List<IdentityAttributeDTO> getIdentityAttributesOfParticipant(String credentialId) {
        var decodedCredentialId = URLDecoder.decode(credentialId, StandardCharsets.UTF_8);
        return mapper.toListIdentityAttributeDTOV1(controller.getIdentityAttributesOfParticipant(decodedCredentialId));
    }

    @Override
    public void validateTierOneSession(TierOneSessionDTO tierOneSessionDTO) {
        controller.validateTierOneSession(mapper.toV0(tierOneSessionDTO));
    }
}
