package at.tuwien.entities.identifier;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.Instant;
import java.util.List;

@Data
@Entity
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Where(clause = "deleted is null")
@EntityListeners(AuditingEntityListener.class)
@SQLDelete(sql = "update mdb_identifiers set deleted = NOW() where id = ?")
@javax.persistence.Table(name = "mdb_identifiers", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"qid", "cid", "dbid"})
})
public class Identifier {

    @Id
    @EqualsAndHashCode.Include
    @ToString.Include
    @GeneratedValue(generator = "database-sequence")
    @GenericGenerator(
            name = "database-sequence",
            strategy = "enhanced-sequence",
            parameters = @org.hibernate.annotations.Parameter(name = "sequence_name", value = "mdb_identifiers_seq")
    )
    private Long id;

    @Column(nullable = false)
    private Long cid;

    @Column(nullable = false)
    private Long dbid;

    @Column(nullable = false)
    private Long qid;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false, columnDefinition = "enum('EVERYONE', 'TRUSTED', 'SELF')")
    @Enumerated(EnumType.STRING)
    private VisibilityType visibility = VisibilityType.SELF;

    @Column
    private String doi;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "identifier")
    private List<Creator> creators;

    @Column(nullable = false, updatable = false)
    @CreatedDate
    private Instant created;

    @Column
    @LastModifiedDate
    private Instant lastModified;

    @Column
    private Instant deleted;

}


