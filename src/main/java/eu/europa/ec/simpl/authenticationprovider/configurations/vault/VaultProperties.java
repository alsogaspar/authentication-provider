package eu.europa.ec.simpl.authenticationprovider.configurations.vault;

import java.net.URI;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = VaultProperties.prefix)
public record VaultProperties(
        URI uri, VaultProperties.Authentication authentication, String secretEngine, String basePath) {
    public static final String prefix = "vault";
    public static final String uriProperty = prefix + ".uri";

    public static record Authentication(String roleId, String secretId, String path) {}
}
