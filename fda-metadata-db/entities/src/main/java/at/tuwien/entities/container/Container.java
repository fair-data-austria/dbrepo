package at.tuwien.entities.container;

import at.tuwien.entities.container.image.ContainerImage;
import at.tuwien.entities.database.Database;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
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
@Where(clause = "deleted is null")
@EntityListeners(AuditingEntityListener.class)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@SQLDelete(sql = "update mdb_container set deleted = NOW() where id = ?")
@Table(name = "mdb_containers")
public class Container {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(generator = "container-sequence")
    @GenericGenerator(
            name = "container-sequence",
            strategy = "enhanced-sequence",
            parameters = @org.hibernate.annotations.Parameter(name = "sequence_name", value = "mdb_containers_seq")
    )
    private Long id;

    @Column(nullable = false)
    private String name;

    @ToString.Exclude
    @Column(nullable = false)
    private String internalName;

    @Column(nullable = false)
    private String hash;

    @Column
    private Integer port;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumns({
            @JoinColumn(name = "id", referencedColumnName = "id", insertable = false, updatable = false)
    })
    private List<Database> databases;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    private ContainerImage image;

    @CreatedDate
    @Column(name = "created", nullable = false, updatable = false)
    private Instant created;

    @Column
    @LastModifiedDate
    private Instant lastModified;

    @Column
    private Instant deleted;

}
