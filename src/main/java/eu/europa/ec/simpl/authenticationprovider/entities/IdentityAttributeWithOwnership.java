package eu.europa.ec.simpl.authenticationprovider.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "identity_attribute")
@Accessors(chain = true)
@Getter
@Setter
@ToString
public class IdentityAttributeWithOwnership {

    @Id
    private UUID id;

    @Column(name = "code")
    private String code;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "assignable_to_roles")
    private boolean assignableToRoles;

    @Column(name = "assigned_to_participant")
    private boolean assignedToParticipant;

    @Column(name = "enabled")
    private boolean enabled;

    @CreationTimestamp
    @Column(name = "creation_timestamp")
    private Instant creationTimestamp;

    @UpdateTimestamp
    @Column(name = "update_timestamp")
    private Instant updateTimestamp;
}
