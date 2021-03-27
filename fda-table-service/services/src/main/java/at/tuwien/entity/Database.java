package at.tuwien.entity;

import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.List;

@Data
@Entity(name = "mdb_database")
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
public class Database extends Auditable {

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Container container;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Boolean isPublic;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Table> tables;

}
