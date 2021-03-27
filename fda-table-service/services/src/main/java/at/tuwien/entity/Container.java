package at.tuwien.entity;

import lombok.*;
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
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
@EntityListeners(AuditingEntityListener.class)
public class Container {

    @Id
    @EqualsAndHashCode.Include
    @ToString.Include
    @Column(nullable = false)
    private String id;

    @Column(nullable = false)
    private Instant containerCreated;

    @Column(nullable = false)
    private String name;

    @OneToOne(optional = false)
    private ContainerImage image;

    @Transient
    private String status;

    @Column
    private String ipAddress;

    @Column(nullable = false, updatable = false)
    @CreatedDate
    Instant created;

    @Column
    @LastModifiedDate
    Instant lastModified;

}
