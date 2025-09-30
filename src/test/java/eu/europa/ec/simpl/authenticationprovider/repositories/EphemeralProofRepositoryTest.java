package eu.europa.ec.simpl.authenticationprovider.repositories;

import static org.mockito.ArgumentMatchers.any;

import eu.europa.ec.simpl.authenticationprovider.exceptions.EphemeralProofNotFoundException;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EphemeralProofRepositoryTest {

    @Spy
    private EphemeralProofRepository ephemeralProofRepository;

    @Test
    void whenFindByIdOrThrowAndIdNotPresent_thenThrow() {
        BDDMockito.given(ephemeralProofRepository.findById(any())).willReturn(Optional.empty());
        var ex = Assertions.catchException(() -> ephemeralProofRepository.findByIdOrThrow("fake"));
        Assertions.assertThat(ex).isNotNull().isInstanceOf(EphemeralProofNotFoundException.class);
    }
}
