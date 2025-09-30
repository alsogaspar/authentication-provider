package eu.europa.ec.simpl.authenticationprovider.controllers;

import static eu.europa.ec.simpl.common.test.TestUtil.a;
import static org.mockito.Mockito.verify;

import eu.europa.ec.simpl.authenticationprovider.services.MtlsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MTLSControllerTest {

    @Mock
    MtlsService mtlsService;

    @InjectMocks
    MTLSController mtlsController;

    @Test
    void ping() {
        var credentialId = a(String.class);
        mtlsController.ping(credentialId);
        verify(mtlsService).ping(credentialId);
    }
}
