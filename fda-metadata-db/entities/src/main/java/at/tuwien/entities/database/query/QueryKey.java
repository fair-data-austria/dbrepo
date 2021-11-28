package at.tuwien.entities.database.query;

import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode
public class QueryKey implements Serializable {

    private Long id;

    private Long qdbid;

    private Long qtid;
}
