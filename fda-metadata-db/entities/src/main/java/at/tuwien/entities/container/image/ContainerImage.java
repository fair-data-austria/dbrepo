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
    @GeneratedValue(generator = "image-sequence")
    @GenericGenerator(
            name = "image-sequence",
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
    @Column
    private String driverClass;

    @ToString.Include
    @Column
    private String dialect;

    @ToString.Include
    @Column
    private String jdbcMethod;

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

    @ToString.Include
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ContainerImageEnvironmentItem> environment;

    @ToString.Include
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "image")
    private List<Container> containers;

    @Column(nullable = false, updatable = false)
    @CreatedDate
    private Instant created;

    @Column
    @LastModifiedDate
    private Instant lastModified;

    @PreRemove
    public void preRemove() {
        this.containers.forEach(container -> {
            container.setImage(null);
        });
    }

}
