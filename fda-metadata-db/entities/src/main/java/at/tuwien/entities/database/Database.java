package at.tuwien.entities.database;

import at.tuwien.entities.container.Container;
import at.tuwien.entities.database.table.Table;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.Instant;
import java.util.List;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "databaseindex", createIndex = false)
@Where(clause = "deleted is null")
@ToString(onlyExplicitlyIncluded = true)
@EntityListeners(AuditingEntityListener.class)
@SQLDelete(sql = "update mdb_databases set deleted = NOW() where id = ?")
@javax.persistence.Table(name = "mdb_databases")
public class Database {

    @Id
    @EqualsAndHashCode.Include
    @ToString.Include
    @GeneratedValue(generator = "database-sequence")
    @GenericGenerator(
            name = "database-sequence",
            strategy = "enhanced-sequence",
            parameters = @org.hibernate.annotations.Parameter(name = "sequence_name", value = "mdb_databases_seq")
    )
    private Long id;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumns({
            @JoinColumn(name = "id", referencedColumnName = "id", insertable = false, updatable = false)
    })
    private Container container;

    @ToString.Include
    @Column(nullable = false)
    private String name;

    @ToString.Include
    @Column(nullable = false)
    private String internalName;

    @ToString.Include
    @Column(nullable = false, updatable = false)
    private String exchange;

    @ToString.Include
    @Column
    private String description;

    @ToString.Exclude
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumns({
            @JoinColumn(name = "tdbid", referencedColumnName = "id", insertable = false, updatable = false)
    })
    private List<Table> tables;

    @ToString.Include
    @Column(nullable = false)
    private Boolean isPublic;

    @Column(nullable = false, updatable = false)
    @CreatedDate
    private Instant created;

    @Column
    @LastModifiedDate
    private Instant lastModified;

    @Column
    private Instant deleted;

}
