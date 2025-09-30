package eu.europa.ec.simpl.authenticationprovider.controllers;

import static eu.europa.ec.simpl.common.test.TestUtil.a;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;

import eu.europa.ec.simpl.api.authenticationprovider.v1.model.SearchIdentityAttributesWithOwnershipFilterParameterDTO;
import eu.europa.ec.simpl.authenticationprovider.mappers.IdentityAttributeMapperV1Impl;
import eu.europa.ec.simpl.authenticationprovider.utils.DtoUtils;
import eu.europa.ec.simpl.common.model.dto.securityattributesprovider.IdentityAttributeWithOwnershipDTO;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@Import({
    IdentityAttributeControllerV1.class,
    IdentityAttributeMapperV1Impl.class,
})
public class IdentityAttributeControllerV1Test {
    @MockitoBean
    private IdentityAttributeController controller;

    @Autowired
    private IdentityAttributeControllerV1 controllerV1;

    /**
     *
     */
    @Test
    void searchIdentityAttributesWithOwnershipTest() {
        Integer page = 0;
        Integer size = 10;
        List<String> sort = List.of();
        var filter = a(SearchIdentityAttributesWithOwnershipFilterParameterDTO.class);
        var resultElementC0 = a(IdentityAttributeWithOwnershipDTO.class);
        var resultC0 = new PageImpl<>(List.of(resultElementC0));

        controllerV1.searchIdentityAttributesWithOwnership(page, size, sort, filter);

        given(controller.search(argThat(f -> DtoUtils.areJsonEquals(f, filter)), argThat(p -> {
                    return p.getPageSize() == page && p.getPageNumber() == page;
                })))
                .willReturn(resultC0);
    }
}
