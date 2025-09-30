package eu.europa.ec.simpl.authenticationprovider.services.impl;

import eu.europa.ec.simpl.authenticationprovider.services.CredentialInitializer;
import eu.europa.ec.simpl.authenticationprovider.services.DatabaseVaultMigrationInitializer;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;

@Service
public class ContextRefreshedEventListener implements ApplicationListener<ContextRefreshedEvent> {

    private final CredentialInitializer credentialInitializer;
    private final DatabaseVaultMigrationInitializer databaseVaultMigrationInitializer;

    public ContextRefreshedEventListener(
            CredentialInitializer credentialInitializer,
            DatabaseVaultMigrationInitializer databaseVaultMigrationInitializer) {
        this.credentialInitializer = credentialInitializer;
        this.databaseVaultMigrationInitializer = databaseVaultMigrationInitializer;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        databaseVaultMigrationInitializer.migrateAll();
        credentialInitializer.init();
    }
}
