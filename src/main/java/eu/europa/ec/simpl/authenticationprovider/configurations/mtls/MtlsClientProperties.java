package eu.europa.ec.simpl.authenticationprovider.configurations.mtls;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "client")
public record MtlsClientProperties(AuthorityProperties authority) {

    public record AuthorityProperties(String url) {}
}
