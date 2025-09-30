package eu.europa.ec.simpl.authenticationprovider.repositories.specification;

import static eu.europa.ec.simpl.common.test.TestUtil.a;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

import eu.europa.ec.simpl.authenticationprovider.entities.IdentityAttributeWithOwnership;
import eu.europa.ec.simpl.authenticationprovider.filters.IdentityAttributeWithOwnershipFilter;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class IdentityAttributeSpecificationTest {

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    Root<IdentityAttributeWithOwnership> root;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    CriteriaQuery<?> query;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    CriteriaBuilder criteriaBuilder;

    @Test
    void toPredicate_withPopulatedFilter_shouldConsiderAllFilters() {

        var filter = spy(a(IdentityAttributeWithOwnershipFilter.class));
        var spec = new IdentityAttributeSpecification(filter);

        spec.toPredicate(root, query, criteriaBuilder);

        then(filter).should().getCode();
        then(filter).should().getName();
        then(filter).should().getEnabled();
        then(filter).should().getAssignedToParticipant();
        then(filter).should().getUpdateTimestampFrom();
        then(filter).should().getUpdateTimestampTo();
    }

    @Test
    void toPredicate_withEmptyFilter_shouldConsiderAllFilters() {

        var filter = mock(IdentityAttributeWithOwnershipFilter.class);
        var spec = new IdentityAttributeSpecification(filter);

        spec.toPredicate(root, query, criteriaBuilder);

        then(filter).should().getCode();
        then(filter).should().getName();
        then(filter).should().getEnabled();
        then(filter).should().getAssignedToParticipant();
        then(filter).should().getUpdateTimestampFrom();
        then(filter).should().getUpdateTimestampTo();
    }
}
