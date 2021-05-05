package at.tuwien.entities.container.image;

import at.tuwien.entities.Auditable;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity(name = "mdb_environment_item")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
public class ContainerImageEnvironmentItem extends Auditable {

    @ToString.Include
    @Column(nullable = false)
    private String key;

    @ToString.Include
    @Column(nullable = false)
    private String value;

}
