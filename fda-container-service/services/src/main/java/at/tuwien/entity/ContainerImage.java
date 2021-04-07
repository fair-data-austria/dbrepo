package at.tuwien.entity;

import lombok.*;

import javax.persistence.*;
import java.time.Instant;
import java.util.List;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "mdb_image", uniqueConstraints = @UniqueConstraint(columnNames = {"repository", "tag"}))
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
public class ContainerImage extends Auditable {

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

    @ToString.Include
    @Column(nullable = false)
    private Boolean local;

    @ToString.Include
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<EnvironmentItem> environment;

}
