package eu.europa.ec.simpl.authenticationprovider.configurations;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "simpl")
public record SimplProperties(Certificate certificate) {
    public record Certificate(String san) {}
}
