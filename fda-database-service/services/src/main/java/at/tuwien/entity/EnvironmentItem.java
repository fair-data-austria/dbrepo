package at.tuwien.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.transaction.Transactional;

@Entity(name = "mdb_environment_item")
@Data
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
