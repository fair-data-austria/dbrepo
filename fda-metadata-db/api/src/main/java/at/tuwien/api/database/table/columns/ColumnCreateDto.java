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
    @ApiModelProperty(name = "primary key", example = "true")
    private Boolean primaryKey;

    @NotNull
    @ApiModelProperty(name = "name", example = "STRING")
    private ColumnTypeDto type;

    @NotNull
    @ApiModelProperty(name = "null values permitted", example = "false")
    private Boolean nullAllowed;

    @ApiModelProperty(name = "check expression")
    private String checkExpression;

    @ApiModelProperty(name = "foreign key")
    private String foreignKey;

}
