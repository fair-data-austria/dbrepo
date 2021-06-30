package at.tuwien.entities.database.table.columns;

import at.tuwien.entities.database.Database;
import at.tuwien.entities.database.table.Table;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@IdClass(TableColumnKey.class)
@ToString(onlyExplicitlyIncluded = true)
@EntityListeners(AuditingEntityListener.class)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@javax.persistence.Table(name = "mdb_columns")
public class TableColumn {

    @Id
    @EqualsAndHashCode.Include
    @ToString.Include
    @GeneratedValue(generator = "column-sequence")
    @GenericGenerator(
            name = "column-sequence",
            strategy = "enhanced-sequence",
            parameters = @org.hibernate.annotations.Parameter(name = "sequence_name", value = "mdb_columns_seq")
    )
    private Long id;

    @Id
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long tid;

    @Id
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long cdbid;

    /**
     * ManyToOne means {@link JoinColumn#name()} is remote attribute,
     * {@link JoinColumn#referencedColumnName()} is local attribute
     */
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumns({
            @JoinColumn(name = "tid", referencedColumnName = "id", insertable = false, updatable = false),
            @JoinColumn(name = "cdbid", referencedColumnName = "tdbid", insertable = false, updatable = false)
    })
    private Table table;

    @ToString.Include
    @Column(nullable = false, name = "cname")
    private String name;

    @ToString.Include
    @Column(nullable = false)
    private String internalName;

    @ToString.Include
    @Column(nullable = false)
    private Boolean isPrimaryKey;

    @ToString.Include
    @Column(nullable = false, name = "datatype")
    @Enumerated(EnumType.STRING)
    private TableColumnType columnType;

    @ToString.Include
    @Column(nullable = false)
    private Boolean isNullAllowed;

    @ToString.Include
    @Column
    private String checkExpression;

    @ToString.Include
    @Column(nullable = false)
    private Integer ordinalPosition;

    @ToString.Include
    @Column
    private String foreignKey;

    @Column(nullable = false, updatable = false)
    @CreatedDate
    private Instant created;

    @Column
    @LastModifiedDate
    private Instant lastModified;

}
