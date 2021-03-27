package at.tuwien.mapper;

import at.tuwien.dto.table.columns.ColumnCreateDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PostgresTableMapper {

    default String columnCreateDtoToString(ColumnCreateDto column) {
        final StringBuilder builder = new StringBuilder()
                .append(column.getName())
                .append(" ")
                .append(column.getType())
                .append(!column.getNullAllowed() ? " NOT " : "")
                .append("NULL")
                .append(column.getCheckExpression().length() > 0 ? " CHECK " + column.getCheckExpression() : "")
                .append(column.getForeignKey().length() > 0 ? " FOREIGN KEY " + column.getForeignKey() : "");
        return builder.toString();
    }

    List<String> columnCreateDtoArrayToStringArray(ColumnCreateDto[] colums);

}
