package eu.europa.ec.simpl.authenticationprovider.services.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import eu.europa.ec.simpl.authenticationprovider.configurations.SecurityProperties;
import eu.europa.ec.simpl.authenticationprovider.repositories.BaseVaultRepository;
import eu.europa.ec.simpl.authenticationprovider.services.MigratorInitializer.MigrationStatus;
import eu.europa.ec.simpl.authenticationprovider.utils.DtoUtils;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.repository.JpaRepository;

@ExtendWith(MockitoExtension.class)
public class AbstractMigratorInitializerTest {

    @Mock
    private JpaRepository<String, String> jpaRepository;

    @Mock
    private BaseVaultRepository<String> vaultRepository;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private SecurityProperties securityProperties;

    private MockAbstractMigratorInitializer initializer;

    @BeforeEach
    public void init() {
        initializer = new MockAbstractMigratorInitializer(jpaRepository, vaultRepository, securityProperties);
    }

    @Test
    void migrate_withLocationDBWithDataOnDB_willSkipMigration() {
        given(securityProperties.secret().location()).willReturn("database");
        given(jpaRepository.count()).willReturn(1L);
        var result = initializer.migrate();
        assertThat(result).isEqualTo(MigrationStatus.SKIP);
    }

    @Test
    void migrate_withLocationDBWithDataOnVaultAndNotDB_willStartMigration() {
        var credential = "credential-1";
        var credentials = List.of(credential);
        given(securityProperties.secret().location()).willReturn("database");
        given(jpaRepository.count()).willReturn(0L);
        given(vaultRepository.count()).willReturn(1);
        given(vaultRepository.findAll()).willReturn(credentials);
        var result = initializer.migrate();
        verify(jpaRepository).save(credential);
        assertThat(result).isEqualTo(MigrationStatus.FROM_VAULT_TO_DB);
    }

    @Test
    void migrate_withLocationVaultWithDataOnVaultAndDB_willSkipMigration() {
        given(securityProperties.secret().location()).willReturn("vault");
        given(vaultRepository.count()).willReturn(1);
        var result = initializer.migrate();
        assertThat(result).isEqualTo(MigrationStatus.SKIP);
    }

    @Test
    void migrate_withLocationVaultWithNoDataOnVaultAndDB_willStartMigration() {
        var credential = "credential-1";
        var credentials = List.of(credential);
        given(securityProperties.secret().location()).willReturn("vault");
        given(jpaRepository.findAll()).willReturn(credentials);
        given(jpaRepository.count()).willReturn(1L);
        var result = initializer.migrate();
        assertThat(result).isEqualTo(MigrationStatus.FROM_DB_TO_VAULT);
        verify(vaultRepository).save(argThat(dto -> DtoUtils.areJsonEquals(dto, credential)));
    }

    private static class MockAbstractMigratorInitializer
            extends AbstractMigratorInitializer<
                    BaseVaultRepository<String>, JpaRepository<String, String>, String, String> {
        public MockAbstractMigratorInitializer(
                JpaRepository<String, String> jpaRepository,
                BaseVaultRepository<String> vaultRepository,
                SecurityProperties securityProperties) {
            super(jpaRepository, vaultRepository, securityProperties);
        }

        @Override
        public String getName() {
            return "junit-resource";
        }

        @Override
        public String fromVaultToEntity(String vault) {
            return vault;
        }

        @Override
        public String fromEntityToVault(String entity) {
            return entity;
        }

        @Override
        public Object getVaultId(String vault) {
            return vault;
        }

        @Override
        public Object getEntityId(String entity) {
            return entity;
        }

        @Override
        public void deleteEntityId(String entity) {
            // Do Nothing
        }
    }
}
