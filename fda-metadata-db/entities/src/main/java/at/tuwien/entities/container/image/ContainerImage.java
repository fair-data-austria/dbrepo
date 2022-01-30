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
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "mdb_images", uniqueConstraints = @UniqueConstraint(columnNames = {"repository", "tag"}))
public class ContainerImage {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(generator = "image-sequence")
    @GenericGenerator(
            name = "image-sequence",
            strategy = "enhanced-sequence",
            parameters = @org.hibernate.annotations.Parameter(name = "sequence_name", value = "mdb_images_seq")
    )
    public Long id;

    @Column(nullable = false)
    private String repository;

    @ToString.Exclude
    @Column
    private String logo;

    @Column(nullable = false)
    private String tag;

    @Column(nullable = false)
    private String driverClass;

    @Column(nullable = false)
    private String dialect;

    @Column(nullable = false)
    private String jdbcMethod;

    @Column
    private String hash;

    @Column
    private Instant compiled;

    @Column
    private Long size;

    @Column(nullable = false)
    private Integer defaultPort;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "iid", insertable = false, updatable = false)
    private List<ContainerImageEnvironmentItem> environment;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "iid", insertable = false, updatable = false)
    private List<ContainerImageDate> dateFormats;

    @ToString.Exclude
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.MERGE, mappedBy = "image")
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
