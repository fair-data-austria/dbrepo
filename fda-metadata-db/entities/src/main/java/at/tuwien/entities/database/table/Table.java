package at.tuwien.entities.database.table;

import at.tuwien.entities.database.Database;
import at.tuwien.entities.database.table.columns.TableColumn;
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
@ToString(onlyExplicitlyIncluded = true)
@EntityListeners(AuditingEntityListener.class)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@javax.persistence.Table(name = "mdb_tables")
public class Table {

    @Id
    @EqualsAndHashCode.Include
    @ToString.Include
    @GeneratedValue(generator = "table-sequence")
    @GenericGenerator(
            name = "table-sequence",
            strategy = "enhanced-sequence",
            parameters = @org.hibernate.annotations.Parameter(name = "sequence_name", value = "mdb_tables_seq")
    )
    private Long id;

    @Id
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long tdbid;

    @ToString.Include
    @Column(nullable = true, name = "dep_id")
    private Long depositId;

    @ToString.Include
    @Column(nullable = false, name = "tname")
    private String name;

    @ToString.Include
    @Column(name = "tdescription")
    private String description;

    @ToString.Include
    @Column(nullable = false)
    private String internalName;

    @ToString.Include
    @Column(nullable = false, updatable = false)
    private String topic;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "tdbid", insertable = false, updatable = false)
    private Database database;

    @ToString.Include
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "table")
    @Field(type = FieldType.Nested)
    private List<TableColumn> columns;

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

