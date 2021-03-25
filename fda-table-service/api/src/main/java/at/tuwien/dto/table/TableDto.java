package at.tuwien.dto.table;

import at.tuwien.dto.table.columns.ColumnDto;
import at.tuwien.dto.table.columns.RowDto;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
public class TableDto extends TableBriefDto {

    @NotBlank
    @Size(min = 3)
    @Parameter(name = "table name", required = true)
    private String name;

    @NotBlank
    @Parameter(name = "table description", required = true)
    private String description;

    @NotBlank
    @Parameter(name = "table columns", required = true)
    private ColumnDto[] columns;

    @NotBlank
    @Parameter(name = "table rows", required = true, description = "must have the same length of columns")
    private RowDto[] rows;

}
