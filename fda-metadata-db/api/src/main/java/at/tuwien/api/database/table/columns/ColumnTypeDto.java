package at.tuwien.api.database.table.columns;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public enum ColumnTypeDto {
    ENUM("enum"), NUMBER("Double"), STRING("String"), TEXT("String"), BOOLEAN("Boolean"), DATE("Date"), BLOB("Blob");

    String representation;

    ColumnTypeDto(String representation) {
        this.representation = representation;
    }
}
