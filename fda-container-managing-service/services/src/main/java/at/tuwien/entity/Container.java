package at.tuwien.entity;

import lombok.*;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
public class Container extends Auditable {

    @Column(nullable = false)
    private String containerId;

    @Column(nullable = false)
    private Instant containerCreated;

    @Column(nullable = false)
    private String name;

    @OneToOne(optional = false, fetch = FetchType.EAGER)
    private ContainerImage image;

    @Transient
    private String status;

    @Column
    private String ipAddress;

}
