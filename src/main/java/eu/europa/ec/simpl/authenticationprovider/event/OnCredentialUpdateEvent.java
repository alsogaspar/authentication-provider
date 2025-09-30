package eu.europa.ec.simpl.authenticationprovider.event;

import eu.europa.ec.simpl.authenticationprovider.services.impl.AbstractCredentialService;
import jakarta.annotation.Nullable;
import lombok.Value;

@Value
public class OnCredentialUpdateEvent {
    @Nullable AbstractCredentialService.Credential newCredential;
}
