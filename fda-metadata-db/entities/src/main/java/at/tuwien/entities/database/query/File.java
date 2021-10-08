package at.tuwien.entities.database.query;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.Instant;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@IdClass(FileKey.class)
@EntityListeners(AuditingEntityListener.class)
@javax.persistence.Table(name = "mdb_files")
public class File {

    @Id
    @EqualsAndHashCode.Include
    @ToString.Include
    @GeneratedValue(generator = "files-sequence")
    @GenericGenerator(
            name = "files-sequence",
            strategy = "enhanced-sequence",
            parameters = @org.hibernate.annotations.Parameter(name = "sequence_name", value = "mdb_files_seq")
    )
    private Long id;

    @Id
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long fqid;

    @Id
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long fdbid;

    @Column(name = "ref_id", nullable = false)
    private String refId;

    @JoinColumns({
            @JoinColumn(name = "fdbid", referencedColumnName = "qdbid", insertable = false, updatable = false),
            @JoinColumn(name = "fqid", referencedColumnName = "id", insertable = false, updatable = false)
    })
    @ManyToOne(fetch = FetchType.LAZY)
    private Query query;

    @Column(nullable = false, updatable = false)
    @CreatedDate
    private Instant created;

    @Column
    @LastModifiedDate
    private Instant lastModified;

}
