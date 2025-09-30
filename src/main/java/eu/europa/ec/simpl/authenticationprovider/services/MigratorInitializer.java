package eu.europa.ec.simpl.authenticationprovider.services;

public interface MigratorInitializer {
    MigrationStatus migrate();

    public enum MigrationStatus {
        FROM_DB_TO_VAULT,
        FROM_VAULT_TO_DB,
        SKIP;
    }
}
