package at.tuwien.entities.database.table.columns;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public enum TableColumnType {
    ENUM,
    NUMBER,
    DECIMAL,
    STRING,
    TEXT,
    BOOLEAN,
    DATE,
    BLOB;
}