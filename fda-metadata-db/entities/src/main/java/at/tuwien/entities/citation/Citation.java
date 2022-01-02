package at.tuwien.entities.citation;

import at.tuwien.entities.container.Container;
import at.tuwien.entities.database.table.Table;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.hibernate.validator.constraints.Length;
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
@Where(clause = "deleted is null")
@ToString
@EntityListeners(AuditingEntityListener.class)
@SQLDelete(sql = "update mdb_citations set deleted = NOW() where id = ?")
@javax.persistence.Table(name = "mdb_citations")
public class Citation {

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

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column
    private String doi;

    @Column(nullable = false)
    private String query;

    @Column(nullable = false, updatable = false)
    @CreatedDate
    private Instant created;

    @Column
    @LastModifiedDate
    private Instant lastModified;

    @Column
    private Instant deleted;

}
