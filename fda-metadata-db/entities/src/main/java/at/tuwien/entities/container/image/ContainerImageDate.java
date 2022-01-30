package at.tuwien.entities.container.image;

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
@IdClass(ContainerImageDateKey.class)
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
    public Long id;

    @Id
    @Column(name = "iid")
    @EqualsAndHashCode.Include
    public Long iid;

    @Column(name = "example", nullable = false)
    public String example;

    @Column(name = "database_format", nullable = false)
    public String databaseFormat;

    @Column(name = "unix_format", nullable = false)
    public String unixFormat;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

}
