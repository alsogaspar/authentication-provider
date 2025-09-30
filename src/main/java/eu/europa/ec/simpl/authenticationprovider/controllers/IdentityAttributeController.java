package eu.europa.ec.simpl.authenticationprovider.controllers;

import eu.europa.ec.simpl.authenticationprovider.filters.IdentityAttributeWithOwnershipFilter;
import eu.europa.ec.simpl.authenticationprovider.services.IdentityAttributeService;
import eu.europa.ec.simpl.common.model.dto.securityattributesprovider.IdentityAttributeWithOwnershipDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("identity-attribute")
public class IdentityAttributeController {

    private final IdentityAttributeService service;

    public IdentityAttributeController(IdentityAttributeService service) {
        this.service = service;
    }

    @Operation(
            summary = "Search identity attributes with ownership",
            description =
                    "Searches for identity attributes with ownership based on the provided filter and pagination settings",
            responses = {
                @ApiResponse(responseCode = "200", description = "Successfully retrieved the identity attributes"),
                @ApiResponse(responseCode = "401", description = "Access denied"),
                @ApiResponse(responseCode = "403", description = "Forbidden: User does not have the required role"),
            })
    @GetMapping("search")
    public Page<IdentityAttributeWithOwnershipDTO> search(
            @ParameterObject IdentityAttributeWithOwnershipFilter filter,
            @PageableDefault(sort = "id") @ParameterObject Pageable pageable) {
        return service.search(filter, pageable);
    }
}
