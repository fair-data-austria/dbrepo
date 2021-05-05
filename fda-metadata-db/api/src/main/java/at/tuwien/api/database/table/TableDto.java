package at.tuwien.api.database.table;

import at.tuwien.api.database.table.columns.RowDto;
import at.tuwien.api.database.table.columns.ColumnDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
public class TableDto extends TableBriefDto {

    @NotBlank
    @ApiModelProperty(name = "table name", example = "Fundamentals")
    private String name;

    @NotBlank
    @ApiModelProperty(name = "table internal name", example = "fundamentals")
    private String internalName;

    @NotBlank
    @ApiModelProperty(name = "table description", example = "SEC 10K annual fillings (2016-2012) ")
    private String description;

    @NotNull
    @ApiModelProperty(name = "table columns")
    private ColumnDto[] columns;

    @NotNull
    @ApiModelProperty(name = "table rows")
    private RowDto[] rows;

}
