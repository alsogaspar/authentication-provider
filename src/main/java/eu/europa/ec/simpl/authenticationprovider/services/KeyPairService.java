package eu.europa.ec.simpl.authenticationprovider.services;

import eu.europa.ec.simpl.authenticationprovider.exceptions.CipherException;
import eu.europa.ec.simpl.common.model.dto.authenticationprovider.KeyPairDTO;

public interface KeyPairService {

    void generateAndStoreKeyPair() throws CipherException;

    KeyPairDTO getInstalledKeyPair() throws CipherException;

    void importKeyPair(KeyPairDTO keypairImportDTO);

    Boolean existsKeyPair();
}
