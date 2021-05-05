package at.tuwien.entities.database.table;

import at.tuwien.entities.Auditable;
import at.tuwien.entities.database.Database;
import at.tuwien.entities.database.table.columns.TableColumn;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;

@Data
@Entity(name = "mdb_tables")
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
public class Table extends Auditable {

    @ToString.Include
    @Column(nullable = false)
    private String name;

    @ToString.Include
    @Column(nullable = false, unique = true)
    private String internalName;

    @ToString.Include
    @Column
    private String description;

    @ToString.Include
    @ManyToOne
    private Database database;

    @ToString.Include
    @OneToMany
    private List<TableColumn> columns;

}

