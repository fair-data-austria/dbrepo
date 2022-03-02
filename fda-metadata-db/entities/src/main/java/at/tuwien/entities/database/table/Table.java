package at.tuwien.entities.database.table;

import at.tuwien.entities.database.Database;
import at.tuwien.entities.database.table.columns.TableColumn;
import at.tuwien.entities.user.User;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.Instant;
import java.util.List;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "tblindex", createIndex = false)
@IdClass(TableKey.class)
@ToString
@EntityListeners(AuditingEntityListener.class)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@javax.persistence.Table(name = "mdb_tables", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"tdbid", "internalName"})
})
public class Table {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(generator = "table-sequence")
    @GenericGenerator(
            name = "table-sequence",
            strategy = "enhanced-sequence",
            parameters = @org.hibernate.annotations.Parameter(name = "sequence_name", value = "mdb_tables_seq")
    )
    private Long id;

    @Id
    @EqualsAndHashCode.Include
    private Long tdbid;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumns({
            @JoinColumn(name = "createdBy", referencedColumnName = "UserID")
    })
    private User creator;

    @Column(nullable = false, name = "tname")
    private String name;

    @Column(nullable = false)
    private String internalName;

    @Column(nullable = false, updatable = false)
    private String topic;

    @Column(name = "tdescription")
    private String description;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "tdbid", insertable = false, updatable = false)
    private Database database;
    
    @OrderBy("ordinalPosition")
    @Field(type = FieldType.Nested)
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "table")
    private List<TableColumn> columns;

    @Column(name = "separator")
    private Character separator = ',';

    @Column(name = "element_null")
    private String nullElement = null;

    @Column(name = "skip_lines")
    private Long skipLines = null;

    @Column(name = "element_true")
    private String trueElement = "1";

    @Column(name = "element_false")
    private String falseElement = "0";

    @Column(nullable = false, updatable = false)
    @CreatedDate
    private Instant created;

    @Column
    @LastModifiedDate
    private Instant lastModified;

    @PreRemove
    public void preRemove() {
        this.database = null;
    }

}

