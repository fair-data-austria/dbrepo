package at.tuwien.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.transaction.Transactional;
import java.time.Instant;
import java.util.List;

@Data
@Entity(name = "mdb_databases")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@EntityListeners(AuditingEntityListener.class)
public class Database {

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
    @ManyToOne
    private Container container;

    @ToString.Include
    @Column(nullable = false)
    private String name;

    @ToString.Exclude
    @Column(nullable = false)
    private String internalName;

    @ToString.Exclude
    @OneToMany
    private List<Table> tables;

    @ToString.Include
    @Column(nullable = false)
    private Boolean isPublic;

}
