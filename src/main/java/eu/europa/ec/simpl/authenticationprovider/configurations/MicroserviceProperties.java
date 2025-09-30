package eu.europa.ec.simpl.authenticationprovider.configurations;

import java.net.URI;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "microservice")
public record MicroserviceProperties(UsersRoles usersRoles, IdentityProvider identityProvider) {
    public record UsersRoles(URI url) {}

    public record IdentityProvider(URI url) {}
}
