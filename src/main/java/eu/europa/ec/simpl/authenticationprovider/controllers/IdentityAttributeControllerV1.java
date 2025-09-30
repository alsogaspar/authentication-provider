package eu.europa.ec.simpl.authenticationprovider.controllers;

import eu.europa.ec.simpl.api.authenticationprovider.v1.exchanges.IdentityAttributesApi;
import eu.europa.ec.simpl.api.authenticationprovider.v1.model.PagedModelIdentityAttributeWithOwnershipDTO;
import eu.europa.ec.simpl.api.authenticationprovider.v1.model.SearchIdentityAttributesWithOwnershipFilterParameterDTO;
import eu.europa.ec.simpl.authenticationprovider.mappers.IdentityAttributeMapperV1;
import eu.europa.ec.simpl.common.utils.SortUtil;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1")
public class IdentityAttributeControllerV1 implements IdentityAttributesApi {

    private final IdentityAttributeController controller;
    private final IdentityAttributeMapperV1 mapper;

    public IdentityAttributeControllerV1(IdentityAttributeController controller, IdentityAttributeMapperV1 mapper) {
        this.controller = controller;
        this.mapper = mapper;
    }

    @Override
    public PagedModelIdentityAttributeWithOwnershipDTO searchIdentityAttributesWithOwnership(
            Integer page,
            Integer size,
            List<String> sort,
            SearchIdentityAttributesWithOwnershipFilterParameterDTO filter) {
        return mapper.toV1(controller.search(mapper.toV0(filter), PageRequest.of(page, size, SortUtil.sortBy(sort))));
    }
}
