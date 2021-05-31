package at.tuwien.entities.container;

import at.tuwien.entities.container.image.ContainerImage;
import at.tuwien.entities.database.Database;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.Instant;
import java.util.List;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EntityListeners(AuditingEntityListener.class)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "mdb_container")
public class Container {

    @Id
    @EqualsAndHashCode.Include
    @ToString.Include
    @GeneratedValue(generator = "sequence-per-entity")
    @GenericGenerator(
            name = "sequence-per-entity",
            strategy = "enhanced-sequence",
            parameters = @org.hibernate.annotations.Parameter(name = "sequence_name", value = "mdb_container_seq")
    )
    private Long id;

    @ToString.Include
    @Column(nullable = false)
    private Instant containerCreated;

    @ToString.Include
    @Column(nullable = false)
    private String name;

    @ToString.Exclude
    @Column(nullable = false)
    private String internalName;

    @ToString.Include
    @Column(nullable = false)
    private String hash;

    @ToString.Include
    @Column
    private Integer port;

    @ToString.Include
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private ContainerImage image;

    @Column(nullable = false, updatable = false)
    @CreatedDate
    private Instant created;

    @Column
    @LastModifiedDate
    private Instant lastModified;

}
