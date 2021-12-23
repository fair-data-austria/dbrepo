package at.tuwien.entities.database.table;

import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode
public class TableKey implements Serializable {

    private Long id;

    private Long tdbid;

}
