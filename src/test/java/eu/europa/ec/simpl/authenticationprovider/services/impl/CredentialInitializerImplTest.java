package eu.europa.ec.simpl.authenticationprovider.services.impl;

import static eu.europa.ec.simpl.common.test.TestUtil.a;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import eu.europa.ec.simpl.authenticationprovider.exceptions.InvalidCredentialException;
import eu.europa.ec.simpl.authenticationprovider.exceptions.RevokedCredentialException;
import eu.europa.ec.simpl.authenticationprovider.services.AgentService;
import eu.europa.ec.simpl.authenticationprovider.services.CredentialService;
import eu.europa.ec.simpl.common.exchanges.usersroles.CredentialExchange;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;

@ExtendWith(MockitoExtension.class)
class CredentialInitializerImplTest {

    @Mock
    private CredentialExchange credentialExchange;

    @Mock
    private CredentialService credentialService;

    @Mock
    private AgentService agentService;

    @InjectMocks
    private CredentialInitializerImpl credentialInitializerImpl;

    @Test
    void testInitGivenNotFindCredentialOnDatabaseShouldCallUsersRoles() {
        var fileContent = "junit-mock-file".getBytes();
        given(credentialService.hasCredential()).willReturn(false);
        given(credentialExchange.downloadLocalCredentials()).willReturn(new ByteArrayResource(fileContent));
        willDoNothing().given(credentialService).validateCredential(any());
        credentialInitializerImpl.init();
        verify(credentialService).insert(argThat(bytes -> {
            Assertions.assertThat(fileContent).as("The credential content").isEqualTo(bytes);
            return true;
        }));
    }

    @Test
    void testInitGivenFindCredentialOnDatabaseShouldNotCallUsersRoles() {
        given(credentialService.hasCredential()).willReturn(true);

        credentialInitializerImpl.init();

        verifyNoInteractions(agentService);
        verify(credentialService, never()).insert(any());
    }

    @Test
    void testGivenRevokedCredentialFromUsersRolesShouldNotInsertIntoDatabase() {
        given(credentialService.hasCredential()).willReturn(false);
        given(credentialExchange.downloadLocalCredentials()).willReturn(a(ByteArrayResource.class));
        willThrow(RevokedCredentialException.class).given(credentialService).validateCredential(any());

        credentialInitializerImpl.init();

        verify(credentialService, never()).insert(any());
    }

    @Test
    void testGivenInvalidCredentialFromUsersRolesShouldNotInsertIntoDatabase() {
        given(credentialService.hasCredential()).willReturn(false);
        given(credentialExchange.downloadLocalCredentials()).willReturn(a(ByteArrayResource.class));
        willThrow(InvalidCredentialException.class).given(credentialService).validateCredential(any());

        credentialInitializerImpl.init();

        verify(credentialService, never()).insert(any());
    }
}
