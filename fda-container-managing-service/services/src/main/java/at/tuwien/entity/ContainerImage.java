package at.tuwien.entity;

import lombok.*;
import org.hibernate.annotations.Immutable;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import java.math.BigInteger;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;

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
    private Integer defaultPort;

    @ElementCollection
    @Immutable
    private Collection<String> environment;

    @Column(nullable = false)
    private Architecture architecture;

    public final String dockerImageName() {
        return repository + ":" + tag;
    }

}
