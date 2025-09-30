package eu.europa.ec.simpl.authenticationprovider.controllers;

import eu.europa.ec.simpl.authenticationprovider.services.MtlsService;
import eu.europa.ec.simpl.common.constants.SimplHeaders;
import eu.europa.ec.simpl.common.model.dto.identityprovider.ParticipantWithIdentityAttributesDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("mtls")
public class MTLSController {

    private final MtlsService mtlsService;

    public MTLSController(MtlsService mtlsService) {
        this.mtlsService = mtlsService;
    }

    @Operation(
            summary = "Ping the participant",
            description = "Performs a ping operation to check the participant's status using its credential id",
            parameters = {
                @Parameter(
                        name = SimplHeaders.CREDENTIAL_ID,
                        description = "The Public Key Hash of the participant",
                        required = true,
                        schema = @Schema(type = "string"))
            },
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Successfully pinged the participant",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = ParticipantWithIdentityAttributesDTO.class))),
                @ApiResponse(responseCode = "404", description = "Participant not found")
            })
    @GetMapping("ping")
    public ParticipantWithIdentityAttributesDTO ping(@RequestHeader(SimplHeaders.CREDENTIAL_ID) String credentialId) {
        return mtlsService.ping(credentialId);
    }

    @Operation(
            summary = "Store Ephemeral Proof",
            description = "Stores the ephemeral proof for a participant identified by their UUID",
            parameters = {
                @Parameter(
                        name = SimplHeaders.CREDENTIAL_ID,
                        description = "The Public Key Hash of the participant",
                        required = true,
                        schema = @Schema(type = "string"))
            },
            requestBody =
                    @io.swagger.v3.oas.annotations.parameters.RequestBody(
                            description = "The ephemeral proof to be stored",
                            required = true,
                            content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE)),
            responses = {
                @ApiResponse(responseCode = "200", description = "Ephemeral proof successfully stored"),
                @ApiResponse(responseCode = "400", description = "Invalid input data"),
                @ApiResponse(responseCode = "404", description = "Participant not found")
            })
    @PostMapping(value = "ephemeral-proof", consumes = MediaType.TEXT_PLAIN_VALUE)
    public void storeCallerEphemeralProof(
            @RequestHeader(SimplHeaders.CREDENTIAL_ID) String credentialId, @RequestBody String ephemeralProof) {
        mtlsService.insertEphemeralProof(credentialId, ephemeralProof);
    }
}
