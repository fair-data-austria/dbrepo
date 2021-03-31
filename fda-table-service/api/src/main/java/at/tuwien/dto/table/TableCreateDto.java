package at.tuwien.dto.table;

import at.tuwien.dto.table.columns.ColumnCreateDto;
import at.tuwien.dto.table.columns.ColumnDto;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

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
