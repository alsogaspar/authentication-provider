package eu.europa.ec.simpl.authenticationprovider.configurations;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "security")
public record SecurityProperties(SecretProperties secret) {
    public static final String completeSecretLocationProperty = "security.secret.location";
    public static final String locationVault = "vault";
    public static final String locationDatabase = "database";

    public static record SecretProperties(String location) {}
}
