package at.tuwien.api.database.table.columns;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public enum ColumnTypeDto {
    ENUM,
    NUMBER,
    STRING,
    TEXT,
    BOOLEAN,
    DATE,
    BLOB;
}
