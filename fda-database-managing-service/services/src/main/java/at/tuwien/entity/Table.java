package at.tuwien.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.Fetch;

import javax.persistence.*;
import javax.transaction.Transactional;
import java.util.List;

@Data
@Entity(name = "mdb_tables")
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
public class Table extends Auditable {

    @ManyToOne(fetch = FetchType.EAGER)
    private Database database;

}

