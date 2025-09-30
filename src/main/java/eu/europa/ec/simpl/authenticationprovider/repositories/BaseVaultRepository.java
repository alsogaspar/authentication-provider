package eu.europa.ec.simpl.authenticationprovider.repositories;

import java.util.List;
import java.util.Optional;
import org.springframework.vault.core.VaultTemplate;

public interface BaseVaultRepository<T> {

    int count();

    List<T> findAll();

    void save(T entity);

    default List<String> findAllId(VaultTemplate vaultTemplate, String path) {
        return Optional.ofNullable(vaultTemplate.list(path)).orElse(List.of());
    }
}
