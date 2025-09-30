package eu.europa.ec.simpl.authenticationprovider.controllers;

import eu.europa.ec.simpl.authenticationprovider.services.CSRService;
import eu.europa.ec.simpl.common.exchanges.authenticationprovider.CSRExchange;
import eu.europa.ec.simpl.common.model.dto.authenticationprovider.DistinguishedNameDTO;
import jakarta.validation.Valid;
import java.util.Optional;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@RestController
@RequestMapping("csr")
public class CSRController implements CSRExchange {

    private final CSRService csrService;

    public CSRController(CSRService csrService) {
        this.csrService = csrService;
    }

    @Override
    public Resource generateCSR(@RequestBody @Valid DistinguishedNameDTO distinguishedNameDTO) {
        Optional.ofNullable(RequestContextHolder.getRequestAttributes())
                .filter(ServletRequestAttributes.class::isInstance)
                .map(ServletRequestAttributes.class::cast)
                .map(ServletRequestAttributes::getResponse)
                .ifPresent(response ->
                        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=csr.pem"));

        return new ByteArrayResource(csrService.generateCSR(distinguishedNameDTO));
    }
}
