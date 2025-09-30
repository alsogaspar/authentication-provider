package eu.europa.ec.simpl.authenticationprovider.services.impl;

import eu.europa.ec.simpl.authenticationprovider.configurations.mtls.MtlsClientBuilder;
import eu.europa.ec.simpl.authenticationprovider.exceptions.InvalidFqdnException;
import eu.europa.ec.simpl.authenticationprovider.services.AgentService;
import eu.europa.ec.simpl.authenticationprovider.services.CredentialId;
import eu.europa.ec.simpl.authenticationprovider.services.CredentialService;
import eu.europa.ec.simpl.authenticationprovider.services.IdentityAttributeService;
import eu.europa.ec.simpl.client.core.adapters.EphemeralProofAdapter;
import eu.europa.ec.simpl.common.constants.Roles;
import eu.europa.ec.simpl.common.exchanges.mtls.AuthorityExchange;
import eu.europa.ec.simpl.common.model.dto.authenticationprovider.EchoDTO;
import eu.europa.ec.simpl.common.model.dto.identityprovider.ParticipantWithIdentityAttributesDTO;
import eu.europa.ec.simpl.common.model.dto.securityattributesprovider.IdentityAttributeDTO;
import eu.europa.ec.simpl.common.model.dto.securityattributesprovider.IdentityAttributeWithOwnershipDTO;
import eu.europa.ec.simpl.common.security.JwtService;
import feign.RetryableException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@Log4j2
public class AgentServiceImpl implements AgentService {

    private static final String HTTPS_VALUE = "https";

    private final AuthorityExchange authorityClient;
    private final IdentityAttributeService identityAttributeService;
    private final JwtService jwtService;
    private final CredentialService credentialService;
    private final MtlsClientBuilder mtlsClientBuilder;
    private final EphemeralProofAdapter ephemeralProofAdapter;

    public AgentServiceImpl(
            AuthorityExchange authorityClient,
            IdentityAttributeService identityAttributeService,
            JwtService jwtService,
            CredentialService credentialService,
            MtlsClientBuilder mtlsClientBuilder,
            EphemeralProofAdapter ephemeralProofAdapter) {
        this.authorityClient = authorityClient;
        this.identityAttributeService = identityAttributeService;
        this.jwtService = jwtService;
        this.credentialService = credentialService;
        this.mtlsClientBuilder = mtlsClientBuilder;
        this.ephemeralProofAdapter = ephemeralProofAdapter;
    }

    @Override
    public List<IdentityAttributeWithOwnershipDTO> getAndSyncIdentityAttributes() {
        var identityAttributes = authorityClient.getIdentityAttributesWithOwnership();
        identityAttributeService.overwriteIdentityAttributes(identityAttributes);
        return identityAttributes;
    }

    @Override
    public List<IdentityAttributeDTO> getParticipantIdentityAttributes(CredentialId credentialId) {
        return authorityClient.getIdentityAttributesByCredentialIdInUri(credentialId.getContent());
    }

    @Override
    public EchoDTO echo() {
        var echoDTO = new EchoDTO();
        echoDTO.setEmail(jwtService.getEmail());
        echoDTO.setUsername(jwtService.getUsername());

        boolean hasCredential = credentialService.hasCredential();
        echoDTO.setMtlsStatus(hasCredential ? EchoDTO.MTLSStatus.SECURED : EchoDTO.MTLSStatus.NOT_SECURED);
        echoDTO.setConnectionStatus(EchoDTO.ConnectionStatus.NOT_CONNECTED);
        echoDTO.setUserIdentityAttributes(jwtService.getIdentityAttributes());

        if (hasCredential) {
            try {
                var authorityEchoResponse = authorityClient.echo();
                if (jwtService.hasRole(Roles.ONBOARDER_M)) {
                    echoDTO.setParticipant(authorityEchoResponse);
                }
                echoDTO.setConnectionStatus(EchoDTO.ConnectionStatus.CONNECTED);
            } catch (RetryableException e) {
                log.error("Authority comunication error", e);
            }
        }

        return echoDTO;
    }

    /**
     * Performs a ping request to the participant identified by the given FQDN.
     * @param fqdn Can be a FQDN or a URL with https scheme, identifying the target participant.
     * @return A {@link ParticipantWithIdentityAttributesDTO} where only {@link ParticipantWithIdentityAttributesDTO#getIdentityAttributes()} is populated.
     */
    @Override
    public ParticipantWithIdentityAttributesDTO ping(String fqdn) {
        return mtlsClientBuilder.buildParticipantClient(toUrl(fqdn)).ping();
    }

    private static String toUrl(String fqdn) {
        try {
            var uri = new URI(fqdn);
            if (uri.isAbsolute()) {
                if (!Objects.equals(HTTPS_VALUE, uri.getScheme())) {
                    throw new InvalidFqdnException(fqdn);
                }
                return uri.toURL().toString();
            }
            return UriComponentsBuilder.newInstance()
                    .scheme(HTTPS_VALUE)
                    .host(fqdn)
                    .toUriString();
        } catch (URISyntaxException | MalformedURLException e) {
            log.error("Failed to convert fqdn in url", e);
            throw new InvalidFqdnException(fqdn);
        }
    }

    @Override
    public String requestEphemeralProofFromAuthority() {
        return ephemeralProofAdapter.loadEphemeralProof().orElseGet(() -> {
            log.info("Ephemeral proof from authority not found in cache, starting request to authority");
            var ephemeralProof = authorityClient.token();
            ephemeralProofAdapter.storeEphemeralProof(ephemeralProof);
            return ephemeralProof;
        });
    }
}
