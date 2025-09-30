package eu.europa.ec.simpl.authenticationprovider.repositories;

import eu.europa.ec.simpl.authenticationprovider.exceptions.EphemeralProofNotFoundException;
import eu.europa.ec.simpl.common.redis.entity.EphemeralProof;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EphemeralProofRepository extends CrudRepository<EphemeralProof, String> {

    default EphemeralProof findByIdOrThrow(String id) {
        return this.findById(id).orElseThrow(EphemeralProofNotFoundException::new);
    }
}
