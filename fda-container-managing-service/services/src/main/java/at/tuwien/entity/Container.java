package at.tuwien.entity;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.Instant;

@Entity(name = "mdb_container")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@EntityListeners(AuditingEntityListener.class)
public class Container {

    @Id
    @EqualsAndHashCode.Include
    @ToString.Include
    @GeneratedValue(generator = "sequence-per-entity")
    @GenericGenerator(
            name = "sequence-per-entity",
            strategy = "enhanced-sequence",
            parameters = @org.hibernate.annotations.Parameter(name = "prefer_sequence_per_entity", value = "true")
    )
    private Long id;

    @Column(nullable = false, updatable = false)
    @CreatedDate
    private Instant created;

    @Column
    @LastModifiedDate
    private Instant lastModified;

    @ToString.Include
    @Column(nullable = false)
    private Instant containerCreated;

    @ToString.Include
    @Column(nullable = false)
    private String name;

    @ToString.Include
    @Column(nullable = false)
    private String hash;

    @ToString.Include
    @Column(nullable = false)
    private ContainerState status;

    @ToString.Include
    @Column
    private Integer port;

    @ToString.Include
    @ManyToOne(fetch = FetchType.EAGER)
    private ContainerImage image;

}
