package eu.europa.ec.simpl.authenticationprovider.services.impl;

import eu.europa.ec.simpl.authenticationprovider.configurations.KeyPairGenerationAlgorithm;
import eu.europa.ec.simpl.authenticationprovider.configurations.SimplProperties;
import eu.europa.ec.simpl.authenticationprovider.exceptions.CipherException;
import eu.europa.ec.simpl.authenticationprovider.services.CSRService;
import eu.europa.ec.simpl.authenticationprovider.services.KeyPairService;
import eu.europa.ec.simpl.common.csr.EllipticCertificateSignRequest;
import eu.europa.ec.simpl.common.model.dto.authenticationprovider.DistinguishedNameDTO;
import java.nio.charset.StandardCharsets;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

@Service
public class CSRServiceImpl implements CSRService {

    private final KeyPairService keyPairService;
    private final KeyPairGenerationAlgorithm keyPairGenerationAlgorithm;
    private final SimplProperties simplProperties;

    public CSRServiceImpl(
            KeyPairService keyPairService,
            KeyPairGenerationAlgorithm keyPairGenerationAlgorithm,
            SimplProperties simplProperties) {
        this.keyPairService = keyPairService;
        this.keyPairGenerationAlgorithm = keyPairGenerationAlgorithm;
        this.simplProperties = simplProperties;
    }

    @SneakyThrows
    @Override
    public byte[] generateCSR(DistinguishedNameDTO distinguishedNameDTO) throws CipherException {
        var keyPair = keyPairService.getInstalledKeyPair();
        var csr = new EllipticCertificateSignRequest(
                distinguishedNameDTO, keyPair, simplProperties.certificate().san(), keyPairGenerationAlgorithm);
        return csr.getRawCsr().getBytes(StandardCharsets.UTF_8);
    }
}
