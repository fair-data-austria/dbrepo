package at.tuwien.dto.table;

import at.tuwien.dto.table.columns.ColumnDto;
import at.tuwien.dto.table.columns.RowDto;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TableDto extends TableBriefDto {

    @Parameter(name = "table name", required = true)
    private String name;

    @Parameter(name = "table description", required = true)
    private String description;

    @Parameter(name = "table columns", required = true)
    private ColumnDto[] columns;

    @Parameter(name = "table rows", required = true, description = "must have the same length of columns")
    private RowDto[] rows;

}
