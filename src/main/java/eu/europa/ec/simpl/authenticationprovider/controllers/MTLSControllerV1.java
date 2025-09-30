package eu.europa.ec.simpl.authenticationprovider.controllers;

import eu.europa.ec.simpl.api.authenticationprovider.v1.exchanges.MtlsApi;
import eu.europa.ec.simpl.api.authenticationprovider.v1.model.ParticipantWithIdentityAttributesDTO;
import eu.europa.ec.simpl.authenticationprovider.mappers.IdentityAttributeMapperV1;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1")
public class MTLSControllerV1 implements MtlsApi {

    private final MTLSController controller;
    private final IdentityAttributeMapperV1 mapper;

    public MTLSControllerV1(MTLSController controller, IdentityAttributeMapperV1 mapper) {
        this.controller = controller;
        this.mapper = mapper;
    }

    @Override
    public ParticipantWithIdentityAttributesDTO ping(String credentialId) {
        return mapper.toV1(controller.ping(credentialId));
    }

    @Override
    public void storeCallerEphemeralProof(String credentialId, String body) {
        controller.storeCallerEphemeralProof(credentialId, body);
    }
}
