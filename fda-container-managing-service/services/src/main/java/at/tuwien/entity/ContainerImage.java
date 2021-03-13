package at.tuwien.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.math.BigInteger;
import java.time.Instant;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
public class ContainerImage extends Auditable {

    @Column(nullable = false)
    private String repository;

    @Column(nullable = false)
    private String tag;

    @Column(nullable = false)
    private String hash;

    @Column(nullable = false)
    private Instant built;

    @Column(nullable = false)
    private BigInteger size;

    @Column(nullable = false)
    private Architecture architecture;

}
