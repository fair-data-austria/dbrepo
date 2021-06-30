package at.tuwien.entities.database.table;

import at.tuwien.entities.database.Database;
import at.tuwien.entities.database.table.columns.TableColumn;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.List;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@IdClass(TableKey.class)
@ToString(onlyExplicitlyIncluded = true)
@EntityListeners(AuditingEntityListener.class)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@javax.persistence.Table(name = "mdb_tables")
public class Table {

    @Id
    @EqualsAndHashCode.Include
    @ToString.Include
    @GeneratedValue(generator = "sequence-per-entity")
    @GenericGenerator(
            name = "sequence-per-entity",
            strategy = "enhanced-sequence",
            parameters = @org.hibernate.annotations.Parameter(name = "sequence_name", value = "mdb_tables_seq")
    )
    private Long id;

    @Id
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long tdbid;

    @ToString.Include
    @Column(nullable = false, name = "tname")
    private String name;

    @ToString.Include
    @Column(nullable = false, unique = true)
    private String internalName;

    @ToString.Include
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "tdbid", insertable = false, updatable = false)
    private Database database;

    @ToString.Include
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumns({
            @JoinColumn(name = "cdbid", insertable = false, updatable = false),
            @JoinColumn(name = "tid", insertable = false, updatable = false),
    })
    private List<TableColumn> columns;

    @Column(nullable = false, updatable = false)
    @CreatedDate
    private Instant created;

    @Column
    @LastModifiedDate
    private Instant lastModified;

}

