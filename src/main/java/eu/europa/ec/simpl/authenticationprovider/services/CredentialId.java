package eu.europa.ec.simpl.authenticationprovider.services;

import jakarta.validation.constraints.NotBlank;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import lombok.Value;

@Value
public class CredentialId {

    public static CredentialId decode(String credentialId) {
        return new CredentialId(URLDecoder.decode(credentialId, StandardCharsets.UTF_8));
    }

    @NotBlank
    String content;

    private CredentialId(String content) {
        this.content = content;
    }
}
