package eu.europa.ec.simpl.authenticationprovider.controllers;

import static eu.europa.ec.simpl.common.test.TestUtil.a;
import static eu.europa.ec.simpl.common.test.TestUtil.an;
import static org.mockito.BDDMockito.then;

import eu.europa.ec.simpl.authenticationprovider.filters.IdentityAttributeWithOwnershipFilter;
import eu.europa.ec.simpl.authenticationprovider.services.IdentityAttributeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class IdentityAttributeControllerTest {

    @Mock
    IdentityAttributeService service;

    @InjectMocks
    IdentityAttributeController controller;

    @Test
    void search_withRoleT1UAR_M_shouldSucceed() {
        var filter = an(IdentityAttributeWithOwnershipFilter.class);
        var pageRequest = a(PageRequest.class);

        // When
        controller.search(filter, pageRequest);
        // Then
        then(service).should().search(filter, pageRequest);
    }
}
