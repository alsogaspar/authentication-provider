package eu.europa.ec.simpl.authenticationprovider.services.impl;

import eu.europa.ec.simpl.authenticationprovider.entities.IdentityAttributeWithOwnership;
import eu.europa.ec.simpl.authenticationprovider.mappers.IdentityAttributeWithOwnershipMapper;
import eu.europa.ec.simpl.common.model.dto.securityattributesprovider.IdentityAttributeDTO;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;
import lombok.Builder;
import lombok.Value;

public class IdentityAttributeUpdateHelper {

    private final Collection<IdentityAttributePair> identityAttributes;

    private final IdentityAttributeWithOwnershipMapper idaMapper;

    public IdentityAttributeUpdateHelper(Config config) {
        this.idaMapper = config.getIdaMapper();
        this.identityAttributes =
                initializeIdentityAttributePairs(config.getIdaFromEphemeralProof(), config.getIdaFromLocalCopy());
    }

    public IdentityAttributeUpdateHelper(
            List<IdentityAttributeDTO> idaFromEphemeralProof,
            List<IdentityAttributeWithOwnership> idaFromLocalCopy,
            IdentityAttributeWithOwnershipMapper idaMapper) {
        this(new Config(idaFromEphemeralProof, idaFromLocalCopy, idaMapper));
    }

    private static Collection<IdentityAttributePair> initializeIdentityAttributePairs(
            List<IdentityAttributeDTO> idaFromEphemeralProof, List<IdentityAttributeWithOwnership> idaFromLocalCopy) {
        Map<String, IdentityAttributePair> identityAttributes = new HashMap<>();
        idaFromLocalCopy.forEach(ida -> addToMap(identityAttributes, ida));
        idaFromEphemeralProof.forEach(ida -> addToMap(identityAttributes, ida));
        return identityAttributes.values();
    }

    private static void addToMap(
            Map<String, IdentityAttributePair> identityAttributes, IdentityAttributeWithOwnership entity) {
        identityAttributes.put(entity.getCode(), new IdentityAttributePair(entity, null));
    }

    private static void addToMap(Map<String, IdentityAttributePair> identityAttributes, IdentityAttributeDTO dto) {
        identityAttributes.compute(
                dto.getCode(),
                (code, pair) -> pair == null
                        ? new IdentityAttributePair(null, dto)
                        : pair.toBuilder().fromEphemeralProof(dto).build());
    }

    public List<IdentityAttributeWithOwnership> getIdasChanges() {
        return identityAttributes.stream().flatMap(this::mergeIdasChanges).toList();
    }

    private Stream<IdentityAttributeWithOwnership> mergeIdasChanges(IdentityAttributePair ida) {
        if (ida.isOnlyInLocalCopy()) {
            return Stream.of(ida.getFromLocalCopy().setAssignedToParticipant(false));
        }
        if (ida.isOnlyInEphemeralProof()) {
            return Stream.of(idaMapper.toEntity(ida.getFromEphemeralProof(), true));
        }
        if (ida.isInBoth()) {
            IdentityAttributeWithOwnership entity = null;
            if (ida.areDifferent()) {
                entity = ida.getFromLocalCopy();
                idaMapper.updateIdentityAttribute(ida.getFromLocalCopy(), ida.getFromEphemeralProof());
            }
            if (!ida.getFromLocalCopy().isAssignedToParticipant()) {
                entity = ida.getFromLocalCopy();
                ida.getFromLocalCopy().setAssignedToParticipant(true);
            }
            return Stream.ofNullable(entity);
        }
        return Stream.of();
    }

    @Value
    public static class Config {
        List<IdentityAttributeDTO> idaFromEphemeralProof;
        List<IdentityAttributeWithOwnership> idaFromLocalCopy;
        IdentityAttributeWithOwnershipMapper idaMapper;
    }

    @Value
    @Builder(toBuilder = true)
    private static class IdentityAttributePair {

        IdentityAttributeWithOwnership fromLocalCopy;
        IdentityAttributeDTO fromEphemeralProof;

        boolean areEquals() {
            return hasFromLocalCopy() && hasFromEphemeralProof() && areEquals(fromLocalCopy, fromEphemeralProof);
        }

        boolean areDifferent() {
            return !areEquals();
        }

        boolean hasFromLocalCopy() {
            return fromLocalCopy != null;
        }

        boolean hasFromEphemeralProof() {
            return fromEphemeralProof != null;
        }

        boolean isOnlyInLocalCopy() {
            return hasFromLocalCopy() && !hasFromEphemeralProof();
        }

        public boolean isOnlyInEphemeralProof() {
            return hasFromEphemeralProof() && !hasFromLocalCopy();
        }

        public boolean isInBoth() {
            return hasFromLocalCopy() && hasFromEphemeralProof();
        }

        private static boolean areEquals(IdentityAttributeWithOwnership entity, IdentityAttributeDTO dto) {
            return Objects.equals(entity.getCode(), dto.getCode())
                    && Objects.equals(entity.getName(), dto.getName())
                    && Objects.equals(entity.getDescription(), dto.getDescription())
                    && Objects.equals(entity.isEnabled(), dto.isEnabled())
                    && Objects.equals(entity.isAssignableToRoles(), dto.isAssignableToRoles());
        }
    }
}
