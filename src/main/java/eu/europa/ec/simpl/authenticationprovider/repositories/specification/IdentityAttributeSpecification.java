package eu.europa.ec.simpl.authenticationprovider.repositories.specification;

import eu.europa.ec.simpl.authenticationprovider.entities.IdentityAttributeWithOwnership;
import eu.europa.ec.simpl.authenticationprovider.entities.IdentityAttributeWithOwnership_;
import eu.europa.ec.simpl.authenticationprovider.filters.IdentityAttributeWithOwnershipFilter;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.time.Instant;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;

@EqualsAndHashCode
public class IdentityAttributeSpecification implements Specification<IdentityAttributeWithOwnership> {
    private static final String LIKE_TEMPLATE = "%%%s%%";

    private final transient IdentityAttributeWithOwnershipFilter filter;

    public IdentityAttributeSpecification(IdentityAttributeWithOwnershipFilter filter) {
        this.filter = filter;
    }

    private static Specification<IdentityAttributeWithOwnership> codeLike(String code) {
        return (root, query, criteriaBuilder) -> code != null
                ? criteriaBuilder.like(
                        criteriaBuilder.lower(root.get(IdentityAttributeWithOwnership_.code)),
                        LIKE_TEMPLATE.formatted(code.toLowerCase()))
                : null;
    }

    private static Specification<IdentityAttributeWithOwnership> nameLike(String name) {
        return (root, query, criteriaBuilder) -> name != null
                ? criteriaBuilder.like(
                        criteriaBuilder.lower(root.get(IdentityAttributeWithOwnership_.name)),
                        LIKE_TEMPLATE.formatted(name.toLowerCase()))
                : null;
    }

    private static Specification<IdentityAttributeWithOwnership> enabledEqual(Boolean enabled) {
        return (root, query, criteriaBuilder) -> enabled != null
                ? criteriaBuilder.equal(root.get(IdentityAttributeWithOwnership_.enabled), enabled)
                : null;
    }

    private static Specification<IdentityAttributeWithOwnership> assignedToParticipantEqual(
            Boolean assignedToParticipant) {
        return (root, query, criteriaBuilder) -> assignedToParticipant != null
                ? criteriaBuilder.equal(
                        root.get(IdentityAttributeWithOwnership_.assignedToParticipant), assignedToParticipant)
                : null;
    }

    private static Specification<IdentityAttributeWithOwnership> updateTimestampGreaterThanOrEqualTo(
            Instant updateTimestampMin) {
        return (root, query, criteriaBuilder) -> updateTimestampMin != null
                ? criteriaBuilder.greaterThanOrEqualTo(
                        root.get(IdentityAttributeWithOwnership_.updateTimestamp), updateTimestampMin)
                : null;
    }

    private static Specification<IdentityAttributeWithOwnership> updateTimestampLessThanOrEqualTo(
            Instant updateTimestampMax) {

        return (root, query, criteriaBuilder) -> updateTimestampMax != null
                ? criteriaBuilder.lessThanOrEqualTo(
                        root.get(IdentityAttributeWithOwnership_.updateTimestamp), updateTimestampMax)
                : null;
    }

    @Override
    public Predicate toPredicate(
            Root<IdentityAttributeWithOwnership> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        return Specification.allOf(
                        codeLike(filter.getCode()),
                        nameLike(filter.getName()),
                        enabledEqual(filter.getEnabled()),
                        assignedToParticipantEqual(filter.getAssignedToParticipant()),
                        updateTimestampGreaterThanOrEqualTo(filter.getUpdateTimestampFrom()),
                        updateTimestampLessThanOrEqualTo(filter.getUpdateTimestampTo()))
                .toPredicate(root, query, criteriaBuilder);
    }
}
