package at.tuwien.entities.database;

import at.tuwien.entities.Auditable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

@Data
@Entity(name = "mdb_tables")
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
public class Table extends Auditable {

    @ToString.Include
    @Column(nullable = false)
    private String name;

    @ToString.Include
    @ManyToOne(fetch = FetchType.EAGER)
    private Database database;

}

