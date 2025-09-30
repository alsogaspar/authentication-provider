package eu.europa.ec.simpl.authenticationprovider.configurations;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "crypto")
public record CryptoProperties(String secretKeyBase64) {}
