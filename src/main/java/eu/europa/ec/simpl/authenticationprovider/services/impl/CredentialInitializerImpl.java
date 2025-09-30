package eu.europa.ec.simpl.authenticationprovider.services.impl;

import eu.europa.ec.simpl.authenticationprovider.exceptions.InvalidCredentialException;
import eu.europa.ec.simpl.authenticationprovider.exceptions.RevokedCredentialException;
import eu.europa.ec.simpl.authenticationprovider.services.CredentialInitializer;
import eu.europa.ec.simpl.authenticationprovider.services.CredentialService;
import eu.europa.ec.simpl.common.exchanges.usersroles.CredentialExchange;
import jakarta.transaction.Transactional;
import java.nio.charset.StandardCharsets;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

@Log4j2
@Service
public class CredentialInitializerImpl implements CredentialInitializer {

    private final CredentialExchange usersRolesCredentialExchange;
    private final CredentialService credentialService;

    public CredentialInitializerImpl(
            CredentialExchange usersRolesCredentialExchange, CredentialService credentialService) {
        this.usersRolesCredentialExchange = usersRolesCredentialExchange;
        this.credentialService = credentialService;
    }

    @Override
    @Transactional
    public void init() {
        log.info("Start credential initializer");
        if (!credentialService.hasCredential()) {
            log.info("Credential not found in the local database. Falling back to users-roles lookup.");
            try {
                byte[] credentialBytes = getCredentialFromUsersRoles();
                credentialService.validateCredential(new String(credentialBytes, StandardCharsets.UTF_8));
                credentialService.insert(credentialBytes);
                log.info("Credential inserted successfully.");
            } catch (HttpClientErrorException.NotFound e) {
                log.warn("Unable to download credential from users-roles", e);
            } catch (InvalidCredentialException | RevokedCredentialException e) {
                log.warn(
                        """
                                Failed to initialize agent with users-roles credential,
                                please manually install a new credential in the agent
                                """,
                        e);
            }
        }
        log.info("End credential initializer");
    }

    @SneakyThrows
    private byte[] getCredentialFromUsersRoles() {
        log.info("Retrieve credential from users-roles");
        return usersRolesCredentialExchange.downloadLocalCredentials().getContentAsByteArray();
    }
}
