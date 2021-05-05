package at.tuwien.entities.database;

import at.tuwien.entities.Auditable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.List;

@Data
@Entity(name = "mdb_views")
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
public class View extends Auditable {

    @ToString.Include
    @Column(nullable = false)
    private String name;

    @ToString.Include
    @OneToMany(fetch = FetchType.EAGER)
    private List<Database> databases;

}
