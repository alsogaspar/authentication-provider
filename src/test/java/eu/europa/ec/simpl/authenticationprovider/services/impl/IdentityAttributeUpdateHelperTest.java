package eu.europa.ec.simpl.authenticationprovider.services.impl;

import static org.assertj.core.api.Assertions.assertThat;

import eu.europa.ec.simpl.authenticationprovider.entities.IdentityAttributeWithOwnership;
import eu.europa.ec.simpl.authenticationprovider.mappers.IdentityAttributeWithOwnershipMapper;
import eu.europa.ec.simpl.common.model.dto.securityattributesprovider.IdentityAttributeDTO;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class IdentityAttributeUpdateHelperTest {

    private static final IdentityAttributeWithOwnershipMapper mapper =
            Mappers.getMapper(IdentityAttributeWithOwnershipMapper.class);

    @Test
    void getIdasChanges_whenAnIdentityAttributeIsRemovedFromTheEphemeralProof_shouldUnassignThatIdentityAttribute() {
        var idaFromLocalCopy = List.of(ida("IDA_1", true), ida("IDA_2", true));

        var idaFromEphemeralProof = List.of(idaDTO("IDA_1"));

        var updateHelper = new IdentityAttributeUpdateHelper(idaFromEphemeralProof, idaFromLocalCopy, mapper);

        assertThat(updateHelper.getIdasChanges())
                .anyMatch(ida -> ida.getCode().equals("IDA_2") && !ida.isAssignedToParticipant())
                .hasSize(1);
    }

    @Test
    void getChanges_whenAnIdentityAttributeFieldIdasChangesInTheEphemeralProof_shouldUpdateThatIdentityAttribute() {
        var idaFromLocalCopy = List.of(ida("IDA_1", true), ida("IDA_2", true));

        var idaFromEphemeralProof =
                List.of(idaDTO("IDA_1", dto -> dto.setDescription("Updated description")), idaDTO("IDA_2"));

        var updateHelper = new IdentityAttributeUpdateHelper(idaFromEphemeralProof, idaFromLocalCopy, mapper);

        assertThat(updateHelper.getIdasChanges())
                .anyMatch(ida ->
                        ida.getCode().equals("IDA_1") && ida.getDescription().equals("Updated description"))
                .hasSize(1);
    }

    @Test
    void getIdasChanges_whenANewIdentityAttributeIsAddedToTheEphemeralProof_shouldCreateThatIdentityAttribute() {
        var idaFromLocalCopy = List.of(ida("IDA_1", true), ida("IDA_2", true));

        var idaFromEphemeralProof = List.of(idaDTO("IDA_1"), idaDTO("IDA_2"), idaDTO("IDA_3"));

        var updateHelper = new IdentityAttributeUpdateHelper(idaFromEphemeralProof, idaFromLocalCopy, mapper);

        assertThat(updateHelper.getIdasChanges())
                .anyMatch(ida -> ida.getCode().equals("IDA_3") && ida.isAssignedToParticipant())
                .hasSize(1);
    }

    @Test
    void
            getIdasChanges_whenAnUnassignedIdentityAttributeIsAddedToTheEphemeralProof_shouldUpdateThatIdentityAttribute() {
        var idaFromLocalCopy = List.of(ida("IDA_1", true), ida("IDA_2", false));

        var idaFromEphemeralProof = List.of(idaDTO("IDA_1"), idaDTO("IDA_2"));

        var updateHelper = new IdentityAttributeUpdateHelper(idaFromEphemeralProof, idaFromLocalCopy, mapper);

        assertThat(updateHelper.getIdasChanges())
                .anyMatch(ida -> ida.getCode().equals("IDA_2") && ida.isAssignedToParticipant())
                .hasSize(1);
    }

    @Test
    void getChanges_whenAttributesInTheEphemeralProofChange_shouldProvideTheCorrespondingIdasChanges() {
        var idaFromLocalCopy = List.of(ida("IDA_1", true), ida("IDA_2", true), ida("IDA_3", false), ida("IDA_4", true));

        var idaFromEphemeralProof = List.of(
                idaDTO("IDA_1"),
                idaDTO("IDA_3"),
                idaDTO("IDA_4", ida -> ida.setDescription("Updated description")),
                idaDTO("IDA_5"));

        var updateHelper = new IdentityAttributeUpdateHelper(idaFromEphemeralProof, idaFromLocalCopy, mapper);

        assertThat(updateHelper.getIdasChanges())
                .anyMatch(ida -> ida.getCode().equals("IDA_2") && !ida.isAssignedToParticipant())
                .anyMatch(ida -> ida.getCode().equals("IDA_3") && ida.isAssignedToParticipant())
                .anyMatch(ida -> ida.getCode().equals("IDA_4")
                        && ida.getDescription().equals("Updated description")
                        && ida.isAssignedToParticipant())
                .anyMatch(ida -> ida.getCode().equals("IDA_5") && ida.isAssignedToParticipant())
                .hasSize(4);
    }

    @Test
    void getChanges_whenNoAttributeInTheEphemeralProofIdasChanges_shouldNotProvideAnyChange() {
        var idaFromLocalCopy = List.of(ida("IDA_1", true), ida("IDA_2", true), ida("IDA_3", true), ida("IDA_4", true));

        var idaFromEphemeralProof = List.of(idaDTO("IDA_1"), idaDTO("IDA_2"), idaDTO("IDA_3"), idaDTO("IDA_4"));

        var updateHelper = new IdentityAttributeUpdateHelper(idaFromEphemeralProof, idaFromLocalCopy, mapper);

        assertThat(updateHelper.getIdasChanges()).isEmpty();
    }

    private IdentityAttributeWithOwnership ida(String label, Boolean assigned) {
        return new IdentityAttributeWithOwnership()
                .setCode(label)
                .setName(label)
                .setDescription(label)
                .setAssignableToRoles(true)
                .setEnabled(true)
                .setAssignedToParticipant(assigned);
        /// TODO delete participant types association
        // .setParticipantTypes(new HashSet<>(Set.of("CONSUMER")));
    }

    private IdentityAttributeDTO idaDTO(String label, Consumer<IdentityAttributeDTO> customizer) {
        var dto = new IdentityAttributeDTO()
                .setCode(label)
                .setName(label)
                .setDescription(label)
                .setAssignableToRoles(true)
                .setEnabled(true)
                .setParticipantTypes(new HashSet<>(Set.of("CONSUMER")));

        customizer.accept(dto);

        return dto;
    }

    private IdentityAttributeDTO idaDTO(String label) {
        return idaDTO(label, dto -> {});
    }
}
