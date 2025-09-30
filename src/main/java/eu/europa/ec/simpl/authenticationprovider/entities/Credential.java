package eu.europa.ec.simpl.authenticationprovider.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Entity
@Table(name = "credential")
@Accessors(chain = true)
@Getter
@Setter
@ToString
public class Credential {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ToString.Exclude
    @Column(name = "content")
    private byte[] content;

    @Column(name = "participant_id")
    private UUID participantId;
}
