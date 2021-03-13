package at.tuwien.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import java.time.Instant;


@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
public class DatabaseContainer extends Auditable {

    @Column(nullable = false)
    private String containerId;

    @Column(nullable = false)
    private Instant containerCreated;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String databaseName;

    @OneToOne(optional = false)
    private ContainerImage image;

    @Transient
    private String status;

    @Column
    private String ipAddress;

}
