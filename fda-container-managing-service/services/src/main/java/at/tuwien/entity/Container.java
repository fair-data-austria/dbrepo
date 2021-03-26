package at.tuwien.entity;

import lombok.*;

import javax.persistence.*;
import java.time.Instant;

@Entity(name = "mdb_container")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
public class Container extends Auditable {

    @Column(nullable = false)
    private String containerHash;

    @Column(nullable = false)
    private Instant containerCreated;

    @Column(nullable = false)
    private String name;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private ContainerImage image;

    @Column(nullable = false)
    private ContainerState status;

    @Column
    private String ipAddress;

}
