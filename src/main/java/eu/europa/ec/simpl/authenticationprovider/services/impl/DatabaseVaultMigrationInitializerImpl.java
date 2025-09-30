package eu.europa.ec.simpl.authenticationprovider.services.impl;

import eu.europa.ec.simpl.authenticationprovider.services.DatabaseVaultMigrationInitializer;
import eu.europa.ec.simpl.authenticationprovider.services.MigratorInitializer;
import java.util.Collections;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Service
public class DatabaseVaultMigrationInitializerImpl implements DatabaseVaultMigrationInitializer {

    private final List<MigratorInitializer> migratorInitializers;

    public DatabaseVaultMigrationInitializerImpl(List<MigratorInitializer> migratorInitializers) {
        this.migratorInitializers = Collections.unmodifiableList(migratorInitializers);
    }

    @Override
    @Transactional
    public void migrateAll() {
        migratorInitializers.forEach(MigratorInitializer::migrate);
    }
}
