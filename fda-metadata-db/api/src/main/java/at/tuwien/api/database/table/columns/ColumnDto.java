package at.tuwien.api.database.table.columns;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
public class ColumnDto {

    @NotBlank
    @ApiModelProperty(name = "name", example = "Price Limit")
    private String name;

    @NotNull
    @ApiModelProperty(name = "primary key", example = "true")
    private Boolean isPrimaryKey;

    @NotNull
    @ApiModelProperty(name = "type", example = "STRING")
    private ColumnTypeDto columnType;

    @NotNull
    @ApiModelProperty(name = "null allowed", example = "true")
    private Boolean isNullAllowed;

    @ApiModelProperty(name = "check constraint", example = "Price Limit > 0")
    private String checkExpression;

    @ApiModelProperty(name = "foreign key")
    private String foreignKey;

}
