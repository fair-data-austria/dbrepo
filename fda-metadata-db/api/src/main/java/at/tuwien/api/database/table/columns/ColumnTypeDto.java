package at.tuwien.api.database.table.columns;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public enum ColumnTypeDto {
    ENUM("enum"),
    NUMBER("java.lang.Double"),
    STRING("java.lang.String"),
    TEXT("java.lang.String"),
    BOOLEAN("java.lang.Boolean"),
    DATE("java.sql.Date"),
    BLOB("java.sql.Blob");

    String representation;

    ColumnTypeDto(String representation) {
        this.representation = representation;
    }
}
