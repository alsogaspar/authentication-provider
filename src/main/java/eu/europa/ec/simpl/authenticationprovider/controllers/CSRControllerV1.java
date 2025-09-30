package eu.europa.ec.simpl.authenticationprovider.controllers;

import eu.europa.ec.simpl.api.authenticationprovider.v1.exchanges.CertificateSignRequestsApi;
import eu.europa.ec.simpl.api.authenticationprovider.v1.model.DistinguishedNameDTO;
import eu.europa.ec.simpl.authenticationprovider.mappers.CSRMapperV1;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1")
public class CSRControllerV1 implements CertificateSignRequestsApi {

    private final CSRController controller;
    private final CSRMapperV1 mapper;

    public CSRControllerV1(CSRController controller, CSRMapperV1 mapper) {
        this.controller = controller;
        this.mapper = mapper;
    }

    @Override
    public Resource generateCSR(DistinguishedNameDTO distinguishedNameDTO) {
        return controller.generateCSR(mapper.toV0(distinguishedNameDTO));
    }
}
