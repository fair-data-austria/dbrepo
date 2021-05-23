package at.tuwien.entities.container.image;

import at.tuwien.entities.container.Container;
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
@Table(name = "mdb_image", uniqueConstraints = @UniqueConstraint(columnNames = {"repository", "tag"}))
public class ContainerImage {

    @Id
    @EqualsAndHashCode.Include
    @ToString.Include
    @GeneratedValue(generator = "sequence-per-entity")
    @GenericGenerator(
            name = "sequence-per-entity",
            strategy = "enhanced-sequence",
            parameters = @org.hibernate.annotations.Parameter(name = "sequence_name", value = "mdb_image_seq")
    )
    public Long id;

    @ToString.Include
    @Column(nullable = false)
    private String repository;

    @ToString.Include
    @Column(nullable = false)
    private String tag;

    @ToString.Include
    @Column(nullable = false)
    private String hash;

    @ToString.Include
    @Column(nullable = false)
    private Instant compiled;

    @ToString.Include
    @Column(nullable = false)
    private Long size;

    @ToString.Include
    @Column(nullable = false)
    private Integer defaultPort;

    @ToString.Exclude
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Container> containers;

    @ToString.Include
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ContainerImageEnvironmentItem> environment;

    @Column(nullable = false, updatable = false)
    @CreatedDate
    private Instant created;

    @Column
    @LastModifiedDate
    private Instant lastModified;

}
