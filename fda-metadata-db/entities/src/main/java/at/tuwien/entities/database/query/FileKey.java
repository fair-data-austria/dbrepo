package at.tuwien.entities.database.query;

import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode
public class FileKey implements Serializable {

    private Long id;

    private Long fdbid;

    private Long fqid;

}
