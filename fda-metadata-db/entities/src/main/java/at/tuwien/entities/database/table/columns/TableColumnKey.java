package at.tuwien.entities.database.table.columns;

import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode
public class TableColumnKey implements Serializable {

    private Long id;

    private Long cdbid;

    private Long tid;

}
