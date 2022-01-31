package at.tuwien.entities.container.image;

import at.tuwien.entities.database.table.columns.TableColumn;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.Instant;

@Data
@Entity
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "mdb_images_date", uniqueConstraints = @UniqueConstraint(columnNames = {"database_format"}))
public class ContainerImageDate {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(generator = "images-date-sequence")
    @GenericGenerator(
            name = "images-date-sequence",
            strategy = "enhanced-sequence",
            parameters = @org.hibernate.annotations.Parameter(name = "sequence_name", value = "mdb_images_date_seq")
    )
    private Long id;

    @Column(name = "iid")
    private Long iid;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "iid", insertable = false, updatable = false)
    private ContainerImage image;

    @Column(name = "example", nullable = false)
    private String example;

    @Column(name = "database_format", nullable = false)
    private String databaseFormat;

    @Column(name = "unix_format", nullable = false)
    private String unixFormat;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

}
