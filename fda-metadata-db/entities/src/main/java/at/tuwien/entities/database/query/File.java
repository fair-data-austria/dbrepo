package at.tuwien.entities.database.query;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
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

    @Column(name = "ref_id", nullable = false)
    private String refId;

    @OneToOne(fetch = FetchType.LAZY)
    private Query query;

}
