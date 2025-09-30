package eu.europa.ec.simpl.authenticationprovider.mappers;

import eu.europa.ec.simpl.authenticationprovider.entities.ApplicantKeyPair;
import eu.europa.ec.simpl.authenticationprovider.repositories.KeyPairVaultRepository;
import eu.europa.ec.simpl.authenticationprovider.services.CryptoService;
import eu.europa.ec.simpl.authenticationprovider.services.impl.AbstractKeyPairService;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper
public interface KeyPairServiceMapper {

    @Mapping(target = "publicKey", qualifiedByName = "encryptMapping")
    @Mapping(target = "privateKey", qualifiedByName = "encryptMapping")
    ApplicantKeyPair toEntity(AbstractKeyPairService.ApplicantKeyPair source, @Context CryptoService cryptoService);

    void fillFromEntity(
            @MappingTarget AbstractKeyPairService.ApplicantKeyPair target,
            ApplicantKeyPair source,
            CryptoService cryptoService);

    @Mapping(target = "publicKey", qualifiedByName = "decryptMapping")
    @Mapping(target = "privateKey", qualifiedByName = "decryptMapping")
    AbstractKeyPairService.ApplicantKeyPair fromEntity(ApplicantKeyPair entity, @Context CryptoService cryptoService);

    @Named("decryptMapping")
    default byte[] decryptMapping(byte[] source, @Context CryptoService cryptoService) {
        return cryptoService.decrypt(source);
    }

    @Named("encryptMapping")
    default byte[] encryptMapping(byte[] source, @Context CryptoService cryptoService) {
        return cryptoService.encrypt(source);
    }

    @Mapping(target = "publicKey", qualifiedByName = "decryptMapping")
    @Mapping(target = "privateKey", qualifiedByName = "decryptMapping")
    KeyPairVaultRepository.ApplicantKeyPair fromEntityToVault(
            ApplicantKeyPair entity, @Context CryptoService cryptoService);

    KeyPairVaultRepository.ApplicantKeyPair toVault(AbstractKeyPairService.ApplicantKeyPair applicantKeyPair);

    AbstractKeyPairService.ApplicantKeyPair fromVault(KeyPairVaultRepository.ApplicantKeyPair applicantKeyPair);

    @Mapping(target = "publicKey", qualifiedByName = "encryptMapping")
    @Mapping(target = "privateKey", qualifiedByName = "encryptMapping")
    ApplicantKeyPair fromVaultToEntity(
            KeyPairVaultRepository.ApplicantKeyPair source, @Context CryptoService cryptoService);
}
