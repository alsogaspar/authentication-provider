package eu.europa.ec.simpl.authenticationprovider.mappers;

import eu.europa.ec.simpl.authenticationprovider.entities.Credential;
import eu.europa.ec.simpl.authenticationprovider.repositories.CredentialVaultRepository;
import eu.europa.ec.simpl.authenticationprovider.services.impl.AbstractCredentialService;
import org.mapstruct.Mapper;

@Mapper
public interface CredentialServiceMapper {
    Credential toEntity(AbstractCredentialService.Credential credential);

    AbstractCredentialService.Credential fromEntity(Credential entity);

    AbstractCredentialService.Credential fromVault(CredentialVaultRepository.Credential credential);

    CredentialVaultRepository.Credential toVault(AbstractCredentialService.Credential credential);

    CredentialVaultRepository.Credential fromEntityToVault(Credential entity);

    Credential fromVaultToEntity(CredentialVaultRepository.Credential credential);
}
