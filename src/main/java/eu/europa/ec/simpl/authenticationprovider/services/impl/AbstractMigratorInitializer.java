package eu.europa.ec.simpl.authenticationprovider.services.impl;

import eu.europa.ec.simpl.authenticationprovider.configurations.SecurityProperties;
import eu.europa.ec.simpl.authenticationprovider.repositories.BaseVaultRepository;
import eu.europa.ec.simpl.authenticationprovider.services.MigratorInitializer;
import java.util.Optional;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.Nullable;

@Log4j2
public abstract class AbstractMigratorInitializer<
                R1 extends BaseVaultRepository<V>, R2 extends JpaRepository<E, ?>, E, V>
        implements MigratorInitializer {

    private final Optional<R1> vaultRepository;
    private final R2 jpaRepository;
    private final SecurityProperties securityProperties;

    protected AbstractMigratorInitializer(
            R2 jpaRepository, @Nullable R1 vaultRepository, SecurityProperties securityProperties) {
        this.vaultRepository = Optional.ofNullable(vaultRepository);
        this.jpaRepository = jpaRepository;
        this.securityProperties = securityProperties;
    }

    public abstract String getName();

    public abstract E fromVaultToEntity(V vault);

    public abstract V fromEntityToVault(E entity);

    public abstract Object getVaultId(V vault);

    public abstract Object getEntityId(E entity);

    public abstract void deleteEntityId(E entity);

    public MigrationStatus migrate() {
        if (needsMigration()) {
            var location = getLocation();
            return switch (location) {
                case SecurityProperties.locationDatabase -> migrateFromVaultToDatabase();
                case SecurityProperties.locationVault -> migrateFromDatabaseToVault();
                default -> throw new IllegalStateException("Invalid location property. Location: " + location);
            };
        } else {
            log.info("Secret " + getName() + " migration was not required.");
            return MigrationStatus.SKIP;
        }
    }

    public boolean needsMigration() {
        var location = getLocation();
        return switch (location) {
            case SecurityProperties.locationDatabase -> needsMigrationToDatabase();
            case SecurityProperties.locationVault -> needsMigrationToVault();
            default -> throw new IllegalStateException("Invalid location property. Location: " + location);
        };
    }

    private String getLocation() {
        return securityProperties.secret().location();
    }

    private boolean needsMigrationToDatabase() {
        var needMigration = false;
        if (jpaRepository.count() != 0) {
            log.info(" migration to database not required: " + getName() + " data already exists.");
            needMigration = false;
        } else if (vaultRepository.isEmpty()) {
            log.info("Database " + getName() + " migration not required: Vault configuration missing.");
            needMigration = false;
        } else if (vaultRepository.orElseThrow().count() == 0) {
            log.info("Database " + getName() + " migration not required: no " + getName() + " present in Vault.");
            needMigration = false;
        } else {
            log.info(" migration to Database required.");
            needMigration = true;
        }
        return needMigration;
    }

    private MigrationStatus migrateFromVaultToDatabase() {
        log.info("Start " + getName() + " migration from Vault to Database");
        var cvr = vaultRepository.orElseThrow();
        cvr.findAll().stream().map(this::fromVaultToEntity).forEach((E c) -> {
            var vaultId = getEntityId(c);
            deleteEntityId(c);
            jpaRepository.save(c);
            log.info("Vault id: {}, " + getName() + " id: {}", vaultId, getEntityId(c));
        });
        return MigrationStatus.FROM_VAULT_TO_DB;
    }

    private MigrationStatus migrateFromDatabaseToVault() {
        log.info("Start " + getName() + " migration from Database to Vault");
        var kvr = vaultRepository.orElseThrow();
        jpaRepository.findAll().stream()
                .map((E entity) -> {
                    log.info(getName() + " id: {}", getEntityId(entity));
                    return fromEntityToVault(entity);
                })
                .forEach(kvr::save);
        return MigrationStatus.FROM_DB_TO_VAULT;
    }

    private boolean needsMigrationToVault() {
        var cvr = vaultRepository.orElseThrow();
        if (cvr.count() > 0) {
            log.info("Vault " + getName() + " migration not required: " + getName() + " present in Vault.");
            return false;
        } else if (jpaRepository.count() == 0) {
            log.info("Vault " + getName() + " migration not required: no " + getName() + " present in Database.");
            return false;
        } else {
            log.info(getName() + "s migration to Vault required.");
            return true;
        }
    }
}
