package at.tuwien.entities.database.table.columns;

import at.tuwien.entities.container.image.ContainerImageDate;
import at.tuwien.entities.database.table.Table;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.Instant;
import java.util.List;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@IdClass(TableColumnKey.class)
@ToString
@EntityListeners(AuditingEntityListener.class)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@javax.persistence.Table(name = "mdb_columns")
public class TableColumn implements Comparable<TableColumn> {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(generator = "column-sequence")
    @GenericGenerator(
            name = "column-sequence",
            strategy = "enhanced-sequence",
            parameters = @org.hibernate.annotations.Parameter(name = "sequence_name", value = "mdb_columns_seq")
    )
    private Long id;

    @Id
    @EqualsAndHashCode.Include
    private Long tid;

    @Id
    @EqualsAndHashCode.Include
    private Long cdbid;

    @Column
    private Long dfid;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "dfid", referencedColumnName = "id", insertable = false, updatable = false)
    private ContainerImageDate dateFormat;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumns({
            @JoinColumn(name = "tid", referencedColumnName = "id", insertable = false, updatable = false),
            @JoinColumn(name = "cdbid", referencedColumnName = "tdbid", insertable = false, updatable = false)
    })
    private Table table;

    @Column(name = "cname", nullable = false)
    private String name;

    @Column(name = "auto_generated")
    private Boolean autoGenerated = false;

    @Column(nullable = false)
    private String internalName;

    @Column(nullable = false)
    private Boolean isPrimaryKey = false;

    @Column(name = "datatype", nullable = false)
    @Enumerated(EnumType.STRING)
    private TableColumnType columnType;

    @Column(nullable = false)
    private Boolean isNullAllowed = true;

    @Column
    private Boolean isUnique;

    @Column
    private String checkExpression;

    @ElementCollection
    @CollectionTable(name = "mdb_columns_enums", joinColumns = {
            @JoinColumn(name = "id", insertable = false, updatable = false),
            @JoinColumn(name = "tid", insertable = false, updatable = false),
            @JoinColumn(name = "edbid", insertable = false, updatable = false)
    })
    private List<String> enumValues;

    @Column(nullable = false)
    private Integer ordinalPosition;

    @Column
    private String foreignKey;

    @Column(name = "reference_table")
    private String references;

    @Column(nullable = false, updatable = false)
    @CreatedDate
    private Instant created;

    @Column
    @LastModifiedDate
    private Instant lastModified;

    @Override
    public int compareTo(TableColumn tableColumn) {
        return Integer.compare(this.ordinalPosition, tableColumn.getOrdinalPosition());
    }
}
