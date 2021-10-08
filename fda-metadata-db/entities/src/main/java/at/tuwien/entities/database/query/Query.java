package at.tuwien.entities.database.query;

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
    private Long qtid;

    @Id
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long qdbid;

    @Column
    private String doi;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String query;

    @Column(nullable = false)
    private String queryNormalized;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "query")
    private List<File> files;

    @Column(name = "deposit_id", unique = true)
    private Long depositId;

    @JoinColumns({
            @JoinColumn(name = "qdbid", referencedColumnName = "tdbid", insertable = false, updatable = false),
            @JoinColumn(name = "qtid", referencedColumnName = "id", insertable = false, updatable = false)
    })
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
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
