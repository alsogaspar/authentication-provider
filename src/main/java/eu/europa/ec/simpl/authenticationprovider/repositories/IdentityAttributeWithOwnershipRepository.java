package eu.europa.ec.simpl.authenticationprovider.repositories;

import eu.europa.ec.simpl.authenticationprovider.entities.IdentityAttributeWithOwnership;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface IdentityAttributeWithOwnershipRepository
        extends JpaRepository<IdentityAttributeWithOwnership, UUID>,
                JpaSpecificationExecutor<IdentityAttributeWithOwnership> {

    List<IdentityAttributeWithOwnership> findAllByCodeIn(List<String> identityAttributesCodes);
}
