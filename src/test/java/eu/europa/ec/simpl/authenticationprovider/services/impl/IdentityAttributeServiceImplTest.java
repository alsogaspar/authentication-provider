package eu.europa.ec.simpl.authenticationprovider.services.impl;

import static eu.europa.ec.simpl.common.test.TestDataUtil.aPageOf;
import static eu.europa.ec.simpl.common.test.TestUtil.a;
import static eu.europa.ec.simpl.common.test.TestUtil.an;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

import eu.europa.ec.simpl.authenticationprovider.entities.IdentityAttributeWithOwnership;
import eu.europa.ec.simpl.authenticationprovider.filters.IdentityAttributeWithOwnershipFilter;
import eu.europa.ec.simpl.authenticationprovider.mappers.IdentityAttributeWithOwnershipMapper;
import eu.europa.ec.simpl.authenticationprovider.repositories.IdentityAttributeWithOwnershipRepository;
import eu.europa.ec.simpl.authenticationprovider.repositories.specification.IdentityAttributeSpecification;
import eu.europa.ec.simpl.authenticationprovider.services.IdentityAttributeRolesService;
import eu.europa.ec.simpl.common.model.dto.securityattributesprovider.IdentityAttributeDTO;
import eu.europa.ec.simpl.common.model.dto.securityattributesprovider.IdentityAttributeWithOwnershipDTO;
import eu.europa.ec.simpl.common.test.MockUtil;
import java.util.List;
import java.util.function.Function;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;

@ExtendWith(MockitoExtension.class)
class IdentityAttributeServiceImplTest {

    @Mock
    IdentityAttributeWithOwnershipRepository idaRepository;

    @Mock
    IdentityAttributeRolesService identityAttributeRolesService;

    @Mock
    IdentityAttributeWithOwnershipMapper mapper;

    @Mock
    IdentityAttributeUpdateHelper updateHelper;

    Function<IdentityAttributeUpdateHelper.Config, IdentityAttributeUpdateHelper> updateHelperFactory =
            MockUtil.spyLambda(config -> updateHelper);

    @InjectMocks
    IdentityAttributeServiceImpl service;

    @Test
    void search_withValidFilterAndPageable_shouldMapRepositoryEntitiesToDTOs() {
        var filter = an(IdentityAttributeWithOwnershipFilter.class);
        var pageRequest = a(PageRequest.class);
        // Given
        var specification = new IdentityAttributeSpecification(filter);
        var entities = aPageOf(IdentityAttributeWithOwnership.class);
        given(idaRepository.findAll(specification, pageRequest)).willReturn(entities);

        // When
        service.search(filter, pageRequest);

        // Then
        then(idaRepository).should().findAll(specification, pageRequest);
        then(mapper).should(times(entities.getSize())).toLightDtoWithOwnership(any());
    }

    @Test
    void overwriteIdentityAttributes() {
        var givenIdentityAttribute = a(IdentityAttributeWithOwnershipDTO.class);
        var givenIdentityAttributes = List.of(givenIdentityAttribute);

        // When
        service.overwriteIdentityAttributes(givenIdentityAttributes);
        // Then
        then(idaRepository).should().deleteAllInBatch();
        then(idaRepository).should(times(givenIdentityAttributes.size())).save(any());
        then(identityAttributeRolesService).should().updateAssignments(any());
    }

    @DirtiesContext
    @Test
    void updateAssignedIdentityAttributes_whenUpdateHelperReturnsChanges_shouldPersistThoseChanges() {
        var change = an(IdentityAttributeWithOwnership.class);
        var idaFromEphemeralProofElement = an(IdentityAttributeDTO.class);
        var changes = List.of(change);
        var idaFromEphemeralProof = List.of(idaFromEphemeralProofElement);

        // Given
        given(updateHelper.getIdasChanges()).willReturn(changes);

        // When
        service.updateAssignedIdentityAttributes(idaFromEphemeralProof);

        // Then
        then(updateHelper).should().getIdasChanges();
        // TODO delete mock identityAttributeRolesService.findAllFetchParticipantTypes
        // then(identityAttributeRolesService).should().findAllFetchParticipantTypes();
        then(idaRepository).should().saveAll(changes);
        then(identityAttributeRolesService)
                .should()
                .updateAssignments(idaFromEphemeralProof.stream()
                        .map(IdentityAttributeDTO::getCode)
                        .toList());
    }

    @DirtiesContext
    @Test
    void updateAssignedIdentityAttributes_whenUpdateHelperReturnsNoChanges_shouldPersistNothing() {
        var idaFromEphemeralProofElement = an(IdentityAttributeDTO.class);
        var idaFromEphemeralProof = List.of(idaFromEphemeralProofElement);
        // Given
        given(updateHelper.getIdasChanges()).willReturn(List.of());

        // When
        service.updateAssignedIdentityAttributes(idaFromEphemeralProof);

        // Then
        then(updateHelper).should().getIdasChanges();
        // TODO delete identityAttributeRolesService.findAllFetchParticipantTypes
        // then(identityAttributeRolesService).should().findAllFetchParticipantTypes();
        then(idaRepository).should(never()).saveAll(any());
        then(identityAttributeRolesService).should(never()).updateAssignments(any());
    }
}
