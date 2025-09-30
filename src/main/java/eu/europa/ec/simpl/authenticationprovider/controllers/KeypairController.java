package eu.europa.ec.simpl.authenticationprovider.controllers;

import eu.europa.ec.simpl.authenticationprovider.exceptions.CipherException;
import eu.europa.ec.simpl.authenticationprovider.services.KeyPairService;
import eu.europa.ec.simpl.authenticationprovider.utils.PemUtils;
import eu.europa.ec.simpl.common.exchanges.authenticationprovider.KeyPairExchange;
import eu.europa.ec.simpl.common.model.dto.authenticationprovider.ImportKeyPairDTO;
import eu.europa.ec.simpl.common.model.dto.authenticationprovider.KeyPairDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class KeypairController implements KeyPairExchange {

    private final KeyPairService keyPairService;

    public KeypairController(KeyPairService keyPairService) {
        this.keyPairService = keyPairService;
    }

    @Operation(
            summary = "Generate Key Pair",
            description = "ONBOARDER_M user generate a public/private Key Pair and store it to database",
            responses = {
                @ApiResponse(responseCode = "204", description = "Key Pair generate and stored successfully"),
                @ApiResponse(responseCode = "401", description = "Unauthorized")
            })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Override
    public void generateKeyPair() throws CipherException {
        keyPairService.generateAndStoreKeyPair();
    }

    @Operation(
            summary = "Keypair Exists",
            description = "ONBOARDER_M checks whether a public/private Key Pair is stored in the database",
            responses = {
                @ApiResponse(responseCode = "200", description = "KeyPair is present"),
                @ApiResponse(responseCode = "404", description = "KeyPair is not present"),
                @ApiResponse(responseCode = "401", description = "Unauthorized")
            })
    @Override
    public ResponseEntity<Void> existsKeypair() {
        return keyPairService.existsKeyPair()
                ? new ResponseEntity<>(HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @Operation(
            summary = "Import Key Pair",
            description = "ONBOARDER_M user import a public/private Key Pair and store it to database",
            responses = {
                @ApiResponse(responseCode = "204", description = "Key Pair generate and stored successfully"),
                @ApiResponse(responseCode = "401", description = "Unauthorized")
            })
    @PostMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void importKeyPair(@RequestBody @Valid ImportKeyPairDTO importKeyPairDTO) {
        var privateKeyBytes = PemUtils.decodePemFormat(importKeyPairDTO.getPrivateKey());
        var publicKeyBytes = PemUtils.decodePemFormat(importKeyPairDTO.getPublicKey());
        var keyPairDTO = new KeyPairDTO(publicKeyBytes, privateKeyBytes);
        keyPairService.importKeyPair(keyPairDTO);
    }

    @Operation(
            summary = "Get installed Key Pair",
            description = "ONBOARDER_M user get the public/private Key Pair",
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description =
                                "Key Pair successfully retrieved. The response body contains the details of the requested KeyPair."),
                @ApiResponse(
                        responseCode = "404",
                        description = "Key Pair not found. The requested KeyPair does not exist or is not accessible."),
                @ApiResponse(responseCode = "401", description = "Unauthorized")
            })
    @Override
    public KeyPairDTO getInstalledKeyPair() {
        return keyPairService.getInstalledKeyPair();
    }
}
