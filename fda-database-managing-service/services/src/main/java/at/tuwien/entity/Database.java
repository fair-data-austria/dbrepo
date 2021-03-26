package at.tuwien.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;

@Data
@Entity(name = "mdb_databases")
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
public class Database extends Auditable {

    @Column(nullable = false)
    private String containerId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Boolean isPublic;

    @OneToMany(fetch = FetchType.EAGER)
    private List<View> views;

    @OneToMany(fetch = FetchType.EAGER)
    private List<Table> tables;

}
