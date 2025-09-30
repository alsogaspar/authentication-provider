package eu.europa.ec.simpl.authenticationprovider.entities;

import eu.europa.ec.simpl.common.entity.annotations.UUIDv7Generator;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "keypair")
@Getter
@Setter
@NoArgsConstructor
public class ApplicantKeyPair {

    public ApplicantKeyPair(byte[] publicKey, byte[] privateKey) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }

    @Id
    @UUIDv7Generator
    private UUID id;

    @Column(name = "public_key")
    private byte[] publicKey;

    @Column(name = "private_key")
    private byte[] privateKey;

    @CreationTimestamp
    @Column(name = "creation_timestamp")
    private Instant creationTimestamp;
}
