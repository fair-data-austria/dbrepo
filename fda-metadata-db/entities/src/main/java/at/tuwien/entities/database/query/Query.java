package at.tuwien.entities.database.query;

import at.tuwien.entities.database.Database;
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
@IdClass(QueryKey.class)
@EntityListeners(AuditingEntityListener.class)
@javax.persistence.Table(name = "mdb_queries")
public class Query {

    @Id
    @EqualsAndHashCode.Include
    @ToString.Include
    @GeneratedValue(generator = "query-sequence")
    @GenericGenerator(
            name = "query-sequence",
            strategy = "enhanced-sequence",
            parameters = @org.hibernate.annotations.Parameter(name = "sequence_name", value = "mdb_queries_seq")
    )
    private Long id;

    @Id
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long qdbid;

    @Id
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long qtid;

    @Column
    private String doi;

    @Column
    private String title;

    @Column
    private String query;

    @Column
    private String description;

    @Column
    private String queryNormalized;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumns({
            @JoinColumn(name = "qdbid", referencedColumnName = "id", insertable = false, updatable = false)
    })
    private Database database;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumns({
            @JoinColumn(name = "qdbid", referencedColumnName = "tdbid", insertable = false, updatable = false),
            @JoinColumn(name = "qtid", referencedColumnName = "id", insertable = false, updatable = false)
    })
    private at.tuwien.entities.database.table.Table table;

    @Column
    private Instant executionTimestamp;

    @Column
    private String resultHash;

    @Column
    private Long resultNumber;

    @Column(nullable = false, updatable = false)
    @CreatedDate
    private Instant created;

    @Column
    @LastModifiedDate
    private Instant lastModified;

}
