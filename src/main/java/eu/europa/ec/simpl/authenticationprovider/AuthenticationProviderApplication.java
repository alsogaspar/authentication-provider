package eu.europa.ec.simpl.authenticationprovider;

import eu.europa.ec.simpl.common.messaging.EnableMessageProducer;
import java.security.Security;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@ConfigurationPropertiesScan
@SpringBootApplication
@EnableMessageProducer
@EnableScheduling
public class AuthenticationProviderApplication {
    public static void main(String[] args) {
        Security.addProvider(new BouncyCastleProvider());
        SpringApplication.run(AuthenticationProviderApplication.class, args);
    }
}
