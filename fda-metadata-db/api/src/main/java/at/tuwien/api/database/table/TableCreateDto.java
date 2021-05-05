package at.tuwien.api.database.table;

import at.tuwien.api.database.table.columns.ColumnCreateDto;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
public class TableCreateDto {

    @NotBlank
    @ApiModelProperty(name = "name", example = "Fundamentals")
    private String name;

    @NotBlank
    @Parameter(name = "table description", required = true, example = "SEC 10K annual fillings (2016-2012) ")
    private String description;

    @NotNull
    @Parameter(name = "table columns", required = true)
    private ColumnCreateDto[] columns;

}
