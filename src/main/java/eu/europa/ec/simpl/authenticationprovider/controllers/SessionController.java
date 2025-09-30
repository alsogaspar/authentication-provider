package eu.europa.ec.simpl.authenticationprovider.controllers;

import eu.europa.ec.simpl.authenticationprovider.services.CredentialId;
import eu.europa.ec.simpl.authenticationprovider.services.SessionService;
import eu.europa.ec.simpl.common.model.dto.authenticationprovider.TierOneSessionDTO;
import eu.europa.ec.simpl.common.model.dto.securityattributesprovider.IdentityAttributeDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("session")
public class SessionController {

    private final SessionService sessionService;

    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @Operation(
            summary = "Retrieve identity attributes of a participant",
            description = "Fetches the identity attributes associated with the specified participant ID",
            parameters = {
                @Parameter(
                        name = "credentialId",
                        description = "The Public Key Hash of the participant",
                        required = true,
                        schema = @Schema(type = "string"))
            },
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Successfully retrieved identity attributes",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        array =
                                                @ArraySchema(
                                                        schema =
                                                                @Schema(implementation = IdentityAttributeDTO.class)))),
                @ApiResponse(responseCode = "404", description = "Ephemeral proof not found")
            })
    @GetMapping("{credentialId}")
    public List<IdentityAttributeDTO> getIdentityAttributesOfParticipant(@PathVariable String credentialId) {
        return sessionService.getIdentityAttributesOfParticipant(CredentialId.decode(credentialId));
    }

    @Operation(
            summary = "Validate Tier 1 session",
            description = "Validate the tier one session against the ephemeral proof stored in the agent",
            responses = {
                @ApiResponse(responseCode = "204", description = "Tier 1 session validated successfully"),
                @ApiResponse(responseCode = "422", description = "Invalid Tier 1 session")
            })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("credential")
    public void validateTierOneSession(@RequestBody TierOneSessionDTO session) {
        sessionService.validateTierOneSession(session);
    }
}
