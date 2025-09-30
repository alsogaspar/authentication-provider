package eu.europa.ec.simpl.authenticationprovider.controllers;

import static eu.europa.ec.simpl.common.test.TestUtil.a;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.times;

import eu.europa.ec.simpl.authenticationprovider.services.CSRService;
import eu.europa.ec.simpl.common.model.dto.authenticationprovider.DistinguishedNameDTO;
import java.io.OutputStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@ExtendWith(MockitoExtension.class)
class CSRControllerTest {

    @Mock
    private CSRService csrService;

    @InjectMocks
    private CSRController csrController;

    @Captor
    private ArgumentCaptor<DistinguishedNameDTO> distinguishedNameCaptor;

    @Captor
    private ArgumentCaptor<OutputStream> outputStreamCaptor;

    @Test
    void generateCSR_shouldSetContentDispositionHeaderAndCallService() throws Exception {
        var distinguishedNameDTO = a(DistinguishedNameDTO.class);
        var mockRequest = a(MockHttpServletRequest.class);
        var mockResponse = a(MockHttpServletResponse.class);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(mockRequest, mockResponse));
        given(csrService.generateCSR(distinguishedNameDTO)).willReturn(a(byte[].class));

        csrController.generateCSR(distinguishedNameDTO);

        assertThat(mockResponse.getHeader(HttpHeaders.CONTENT_DISPOSITION)).isEqualTo("attachment; filename=csr.pem");

        then(csrService).should(times(1)).generateCSR(eq(distinguishedNameDTO));
    }
}
