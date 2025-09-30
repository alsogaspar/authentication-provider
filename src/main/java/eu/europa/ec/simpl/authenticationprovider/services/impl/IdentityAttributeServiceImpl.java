package eu.europa.ec.simpl.authenticationprovider.services.impl;

import eu.europa.ec.simpl.authenticationprovider.filters.IdentityAttributeWithOwnershipFilter;
import eu.europa.ec.simpl.authenticationprovider.mappers.IdentityAttributeWithOwnershipMapper;
import eu.europa.ec.simpl.authenticationprovider.repositories.IdentityAttributeWithOwnershipRepository;
import eu.europa.ec.simpl.authenticationprovider.repositories.specification.IdentityAttributeSpecification;
import eu.europa.ec.simpl.authenticationprovider.services.IdentityAttributeRolesService;
import eu.europa.ec.simpl.authenticationprovider.services.IdentityAttributeService;
import eu.europa.ec.simpl.common.messaging.kafka.outbox.MessagePublisher;
import eu.europa.ec.simpl.common.model.dto.securityattributesprovider.IdentityAttributeDTO;
import eu.europa.ec.simpl.common.model.dto.securityattributesprovider.IdentityAttributeWithOwnershipDTO;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class IdentityAttributeServiceImpl implements IdentityAttributeService {

    private final IdentityAttributeWithOwnershipMapper idaMapper;
    private final IdentityAttributeWithOwnershipRepository idaRepository;
    private final IdentityAttributeRolesService identityAttributeRolesService;
    private final MessagePublisher messagePublisher;
    private final Function<IdentityAttributeUpdateHelper.Config, IdentityAttributeUpdateHelper> updateHelperFactory;
    private final String topicPrefix;

    public IdentityAttributeServiceImpl(
            IdentityAttributeWithOwnershipMapper idaMapper,
            IdentityAttributeWithOwnershipRepository idaRepository,
            IdentityAttributeRolesService identityAttributeRolesService,
            MessagePublisher messagePublisher,
            @Autowired(required = false)
                    Function<IdentityAttributeUpdateHelper.Config, IdentityAttributeUpdateHelper> updateHelperFactory,
            @Value("${simpl.kafka.topic.prefix}") String topicPrefix) {
        this.idaMapper = idaMapper;
        this.idaRepository = idaRepository;
        this.identityAttributeRolesService = identityAttributeRolesService;
        this.messagePublisher = messagePublisher;
        this.updateHelperFactory = Objects.requireNonNullElse(updateHelperFactory, IdentityAttributeUpdateHelper::new);
        this.topicPrefix = topicPrefix;
    }

    @Override
    public Page<IdentityAttributeWithOwnershipDTO> search(
            IdentityAttributeWithOwnershipFilter request, Pageable pageable) {
        return idaRepository
                .findAll(new IdentityAttributeSpecification(request), pageable)
                .map(idaMapper::toLightDtoWithOwnership);
    }

    @Override
    @Transactional
    public void overwriteIdentityAttributes(List<IdentityAttributeWithOwnershipDTO> identityAttributes) {

        idaRepository.deleteAllInBatch();
        identityAttributes.stream().map(idaMapper::toEntity).forEach(idaRepository::save);
        identityAttributeRolesService.updateAssignments(identityAttributes.stream()
                .filter(IdentityAttributeWithOwnershipDTO::getAssignedToParticipant)
                .map(ida -> ida.getIdentityAttribute().getCode())
                .toList());
    }

    @Override
    @Transactional
    public void updateAssignedIdentityAttributes(List<IdentityAttributeDTO> idaFromEphemeralProof) {

        var idaFromLocalCopy = idaRepository.findAll();
        var updateHelper = updateHelperFactory.apply(
                new IdentityAttributeUpdateHelper.Config(idaFromEphemeralProof, idaFromLocalCopy, idaMapper));
        var changes = updateHelper.getIdasChanges();
        if (!changes.isEmpty()) {
            idaRepository.saveAll(changes);
            identityAttributeRolesService.updateAssignments(idaFromEphemeralProof.stream()
                    .map(IdentityAttributeDTO::getCode)
                    .toList());
        }
    }
}
