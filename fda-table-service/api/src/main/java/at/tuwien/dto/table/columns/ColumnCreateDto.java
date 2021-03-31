package at.tuwien.dto.table.columns;

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
public class ColumnCreateDto {

    @NotBlank
    @ApiModelProperty(name = "name", example = "Ticker Symbol")
    private String name;

    @NotNull
    @ApiModelProperty(name = "primary key", example = "false")
    private Boolean primaryKey = true;

    @NotNull
    @ApiModelProperty(name = "name", example = "STRING")
    private ColumnTypeDto type;

    @NotNull
    @ApiModelProperty(name = "null values permitted", example = "true")
    private Boolean nullAllowed = true;

    @ApiModelProperty(name = "check expression", example = "column > 0")
    private String checkExpression;

    @ApiModelProperty(name = "foreign key")
    private String foreignKey;

}
