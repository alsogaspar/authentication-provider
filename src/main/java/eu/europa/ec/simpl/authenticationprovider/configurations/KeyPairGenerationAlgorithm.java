package eu.europa.ec.simpl.authenticationprovider.configurations;

import eu.europa.ec.simpl.common.csr.AlgorithmConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "keypair")
public record KeyPairGenerationAlgorithm(String signatureAlgorithm, String algorithm, int keyLength)
        implements AlgorithmConfig {}
