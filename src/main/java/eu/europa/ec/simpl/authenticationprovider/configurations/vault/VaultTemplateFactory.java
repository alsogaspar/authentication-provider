package eu.europa.ec.simpl.authenticationprovider.configurations.vault;

import java.util.function.Supplier;
import org.springframework.vault.core.VaultTemplate;

public interface VaultTemplateFactory extends Supplier<VaultTemplate> {}
