package at.tuwien.entity;

import lombok.*;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
public class EnvironmentItem extends Auditable {

    @Column(nullable = false)
    private String key;

    @Column(nullable = false)
    private String value;

}
