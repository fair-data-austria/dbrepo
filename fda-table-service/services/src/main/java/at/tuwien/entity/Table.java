package at.tuwien.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Min;

@Data
@Entity(name = "mdb_table")
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
public class Table extends Auditable {

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Database database;

    @Column
    private String description;

}

