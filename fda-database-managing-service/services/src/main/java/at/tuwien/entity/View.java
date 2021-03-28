package at.tuwien.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.transaction.Transactional;
import java.util.List;

@Data
@Entity(name = "mdb_views")
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
public class View extends Auditable {

    @Column
    private String name;

    @OneToMany(fetch = FetchType.EAGER)
    private List<Database> databases;

}
