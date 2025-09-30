package eu.europa.ec.simpl.authenticationprovider.services;

import eu.europa.ec.simpl.authenticationprovider.exceptions.CipherException;
import eu.europa.ec.simpl.common.model.dto.authenticationprovider.DistinguishedNameDTO;

public interface CSRService {

    byte[] generateCSR(DistinguishedNameDTO certificationRequestDTO) throws CipherException;
}
