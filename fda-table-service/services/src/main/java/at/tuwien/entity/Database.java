package at.tuwien.entity;

import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.List;

@Data
@Entity(name = "ddatabase")
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
public class Database extends Auditable {

    @Column(nullable = false)
    private String containerId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Boolean isPublic;

    @OneToOne
    private View view;

    /** @apiNote cascade creations and deletions, hibernate does this for us */
    @OneToMany(cascade = CascadeType.ALL)
    private List<Table> tables;

}
