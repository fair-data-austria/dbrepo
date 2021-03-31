package at.tuwien.entity;

import lombok.*;

import javax.persistence.*;
import java.time.Instant;

@Entity(name = "mdb_environment_item")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
public class EnvironmentItem extends Auditable {

    @ToString.Include
    @Column(nullable = false)
    private String key;

    @ToString.Include
    @Column(nullable = false)
    private String value;

}
