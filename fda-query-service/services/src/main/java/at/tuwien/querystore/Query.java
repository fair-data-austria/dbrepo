package at.tuwien.querystore;

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
@Table(name = "qs_queries")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EntityListeners(AuditingEntityListener.class)
public class Query implements Serializable {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(generator = "database-sequence")
    @GenericGenerator(
            name = "database-sequence",
            strategy = "enhanced-sequence",
            parameters = @org.hibernate.annotations.Parameter(name = "sequence_name", value = "qs_seq")
    )
    private Long id;

    @Column(name = "database_id", nullable = false)
    private Long databaseId;

    @Column
    private Instant execution;

    @Column(nullable = false)
    private String query;

    @Column(name = "query_normalized")
    private String queryNormalized;

    @Column(name = "query_hash", nullable = false)
    private String queryHash;

    @Column(name = "result_hash")
    private String resultHash;

    @Column(name = "result_number")
    private Long resultNumber;

    @Column(nullable = false, updatable = false)
    @CreatedDate
    private Instant created;

    @Column(name = "last_modified")
    @LastModifiedDate
    private Instant lastModified;

}
