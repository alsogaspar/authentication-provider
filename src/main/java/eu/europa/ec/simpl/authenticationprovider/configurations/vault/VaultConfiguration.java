package eu.europa.ec.simpl.authenticationprovider.configurations.vault;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.vault.authentication.AppRoleAuthentication;
import org.springframework.vault.authentication.AppRoleAuthenticationOptions;
import org.springframework.vault.authentication.AppRoleAuthenticationOptions.RoleId;
import org.springframework.vault.authentication.AppRoleAuthenticationOptions.SecretId;
import org.springframework.vault.client.VaultEndpoint;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Log4j2
@Configuration
@ConditionalOnProperty(VaultProperties.uriProperty)
public class VaultConfiguration {

    @Bean
    public VaultTemplateFactory vaultTemplateFactory(VaultProperties vaultProperties) {
        log.info("Create VaultTemplate bean.");
        var vaultUri = vaultProperties.uri();
        log.info("Vault URI: {}", vaultUri);
        var vaultEndpoint = VaultEndpoint.from(vaultUri);
        var options = AppRoleAuthenticationOptions.builder()
                .path(vaultProperties.authentication().path())
                .roleId(RoleId.provided(vaultProperties.authentication().roleId()))
                .secretId(SecretId.provided(vaultProperties.authentication().secretId()))
                .build();
        var restTemplate = new RestTemplate();
        restTemplate.setUriTemplateHandler(new DefaultUriBuilderFactory(vaultUri.toString() + "/v1/"));
        var clientAuthentication = new AppRoleAuthentication(options, restTemplate);
        return () -> new VaultTemplate(vaultEndpoint, clientAuthentication);
    }
}
