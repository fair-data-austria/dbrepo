package at.tuwien.entities.container.image;

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
@ToString(onlyExplicitlyIncluded = true)
@EntityListeners(AuditingEntityListener.class)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "mdb_image_environment_item")
public class ContainerImageEnvironmentItem {

    @Id
    @EqualsAndHashCode.Include
    @ToString.Include
    @GeneratedValue(generator = "environment-sequence")
    @GenericGenerator(
            name = "environment-sequence",
            strategy = "enhanced-sequence",
            parameters = @org.hibernate.annotations.Parameter(name = "sequence_name", value = "mdb_image_environment_item_seq")
    )
    public Long id;

    @ToString.Include
    @Column(nullable = false)
    private String key;

    @ToString.Include
    @Column(nullable = false)
    private String value;

    @ToString.Include
    @Column(nullable = false, name = "etype")
    private ContainerImageEnvironmentItemType type;

    @Column(nullable = false, updatable = false)
    @CreatedDate
    private Instant created;

    @Column
    @LastModifiedDate
    private Instant lastModified;

}
