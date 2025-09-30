package eu.europa.ec.simpl.authenticationprovider.services.impl;

import static eu.europa.ec.simpl.common.test.TestUtil.an;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import eu.europa.ec.simpl.authenticationprovider.configurations.KeyPairGenerationAlgorithm;
import eu.europa.ec.simpl.authenticationprovider.mappers.KeyPairServiceMapperImpl;
import eu.europa.ec.simpl.authenticationprovider.repositories.KeyPairVaultRepository;
import eu.europa.ec.simpl.authenticationprovider.services.impl.AbstractKeyPairService.ApplicantKeyPair;
import eu.europa.ec.simpl.authenticationprovider.utils.DtoUtils;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@Import({
    KeyPairVaultServiceImpl.class,
    KeyPairServiceMapperImpl.class,
})
@TestPropertySource(
        properties = {
            "security.secret.location=vault",
        })
public class KeyPairVaultServiceImplTest {
    @MockitoBean
    private KeyPairGenerationAlgorithm keyPairGenerationAlgorithm;

    @MockitoBean
    private ApplicationEventPublisher applicationEventPublisher;

    @MockitoBean
    private KeyPairVaultRepository repository;

    @Autowired
    private KeyPairVaultServiceImpl service;

    @Test
    void saveTest() {
        var applicantKeyPair = an(ApplicantKeyPair.class);
        service.save(applicantKeyPair);
        verify(repository).save(argThat(arg -> DtoUtils.areJsonEquals(arg, applicantKeyPair)));
    }

    @Test
    void getKeyPairTest() {
        var applicantKeyPair = an(KeyPairVaultRepository.ApplicantKeyPair.class);
        given(repository.getKeyPair()).willReturn(Optional.of(applicantKeyPair));
        var result = service.getKeyPair().orElseThrow();
        assertThat(DtoUtils.areJsonEquals(result, applicantKeyPair)).isTrue();
    }
}
