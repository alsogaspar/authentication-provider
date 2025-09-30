package eu.europa.ec.simpl.authenticationprovider.controllers;

import eu.europa.ec.simpl.api.authenticationprovider.v1.exchanges.KeypairsApi;
import eu.europa.ec.simpl.api.authenticationprovider.v1.model.ImportKeyPairDTO;
import eu.europa.ec.simpl.api.authenticationprovider.v1.model.KeyPairDTO;
import eu.europa.ec.simpl.authenticationprovider.mappers.KeypairMapperV1;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1")
public class KeypairControllerV1 implements KeypairsApi {

    private final KeypairController controller;
    private final KeypairMapperV1 mapper;

    public KeypairControllerV1(KeypairController controller, KeypairMapperV1 mapper) {
        this.controller = controller;
        this.mapper = mapper;
    }

    @Override
    public void existsKeypair() {
        controller.existsKeypair();
    }

    @Override
    public void generateKeyPair() {
        controller.generateKeyPair();
    }

    @Override
    public KeyPairDTO getInstalledKeyPair() {
        return mapper.toV1(controller.getInstalledKeyPair());
    }

    @Override
    public void importKeyPair(ImportKeyPairDTO importKeyPairDTO) {
        controller.importKeyPair(mapper.toV0(importKeyPairDTO));
    }
}
