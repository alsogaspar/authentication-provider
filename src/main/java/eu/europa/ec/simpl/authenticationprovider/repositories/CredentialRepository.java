package eu.europa.ec.simpl.authenticationprovider.repositories;

import eu.europa.ec.simpl.authenticationprovider.entities.Credential;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CredentialRepository extends JpaRepository<Credential, Long> {}
