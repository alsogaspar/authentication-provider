package eu.europa.ec.simpl.authenticationprovider.filters;

import java.time.Instant;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class IdentityAttributeWithOwnershipFilter {
    private String code;
    private String name;
    private Boolean enabled;
    private String participantTypeIn;
    private String participantTypeNotIn;
    private Boolean assignedToParticipant;
    private Instant updateTimestampFrom;
    private Instant updateTimestampTo;
}
