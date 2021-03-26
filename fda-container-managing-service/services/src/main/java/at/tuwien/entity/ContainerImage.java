package at.tuwien.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hibernate.annotations.Immutable;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import java.math.BigInteger;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Entity(name = "mdb_image")
@Data
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

    @ElementCollection
    @Immutable
    private Collection<String> environment;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return this.id;
    }

}
