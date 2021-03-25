package at.tuwien.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.validation.constraints.Min;

@Data
@Entity(name = "ttable")
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
public class Table extends Auditable {

    @OneToOne
    private Database database;

    @Column
    @Min(value = 5)
    private String description;

}

