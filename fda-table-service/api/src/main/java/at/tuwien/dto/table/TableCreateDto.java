package at.tuwien.dto.table;

import at.tuwien.dto.table.columns.ColumnCreateDto;
import at.tuwien.dto.table.columns.ColumnDto;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
public class TableCreateDto {

    @NotBlank
    @Size(min = 3)
    @Parameter(name = "table name", required = true)
    private String name;

    @NotBlank
    @Parameter(name = "table description", required = true)
    private String description;

    @NotBlank
    @Parameter(name = "table columns", required = true)
    private ColumnCreateDto[] columns;

}
