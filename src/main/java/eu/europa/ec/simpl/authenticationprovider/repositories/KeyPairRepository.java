package eu.europa.ec.simpl.authenticationprovider.repositories;

import eu.europa.ec.simpl.authenticationprovider.entities.ApplicantKeyPair;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KeyPairRepository extends JpaRepository<ApplicantKeyPair, UUID> {

    List<ApplicantKeyPair> findAllByOrderByCreationTimestampDesc();
}
