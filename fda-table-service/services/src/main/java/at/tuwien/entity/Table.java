package at.tuwien.entity;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.Instant;
import java.util.List;

@Data
@Entity(name = "mdb_table")
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@EntityListeners(AuditingEntityListener.class)
public class Table {

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

    @CreatedDate
    @ToString.Exclude
    @Column(nullable = false, updatable = false)
    private Instant created;

    @Column
    @ToString.Exclude
    @LastModifiedDate
    private Instant lastModified;

    @ToString.Exclude
    @OneToOne
    private Database database;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "table_id")
    private List<TableColumn> columns;

    @Column
    private String description;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String internalName;

}

