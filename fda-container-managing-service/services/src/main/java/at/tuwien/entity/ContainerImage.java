package at.tuwien.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.time.Instant;
import java.util.List;

@Data
@Entity
@Table(name = "mdb_image", uniqueConstraints = @UniqueConstraint(columnNames = {"repository", "tag"}))
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
    private Instant compiled;

    @Column(nullable = false)
    private Long size;

    @Column(nullable = false)
    private Integer defaultPort;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<EnvironmentItem> environment;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return this.id;
    }

}
