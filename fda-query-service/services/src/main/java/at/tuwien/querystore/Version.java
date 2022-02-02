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
@Table(name = "qs_versions")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EntityListeners(AuditingEntityListener.class)
public class Version implements Serializable {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(generator = "version-sequence")
    @GenericGenerator(
            name = "version-sequence",
            strategy = "enhanced-sequence",
            parameters = @org.hibernate.annotations.Parameter(name = "sequence_name", value = "qs_versions_seq")
    )
    private Long id;

    @Column(nullable = false, updatable = false)
    @CreatedDate
    private Instant created;

}
