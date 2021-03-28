package at.tuwien.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.transaction.Transactional;
import java.time.Instant;

@Entity(name = "mdb_container")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
@EntityListeners(AuditingEntityListener.class)
public class Container extends Auditable {

    @Column(nullable = false)
    private Instant containerCreated;

    @Column(nullable = false)
    private String name;

    @Column
    private String ipAddress;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private ContainerImage image;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

}
