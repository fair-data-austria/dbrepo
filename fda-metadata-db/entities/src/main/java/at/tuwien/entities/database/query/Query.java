package at.tuwien.entities.database.query;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.Instant;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
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

    @Column
    private String doi;

    @Column(nullable = false)
    private String title;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private File file;

    @Column(name = "deposit_id")
    private Long depositId;

    @Column
    private Instant executionTimestamp;

    @Column
    private String resultHash;

    @Column
    private Integer resultNumber;


}
