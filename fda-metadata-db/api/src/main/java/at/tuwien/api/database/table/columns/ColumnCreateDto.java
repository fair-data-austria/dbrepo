package at.tuwien.api.database.table.columns;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
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
