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
    @ApiModelProperty(name = "name", example = "Date")
    private String name;

    @NotNull
    @ApiModelProperty(name = "primary key", example = "true")
    private Boolean primaryKey;

    @NotNull
    @ApiModelProperty(name = "column type", example = "STRING")
    private ColumnTypeDto type;

    @NotNull
    @ApiModelProperty(name = "null value", example = "false")
    private Boolean nullAllowed;

    @NotNull
    @ApiModelProperty(name = "unique", example = "true")
    private Boolean unique;

    @ApiModelProperty(name = "check expression", example = "null")
    private String checkExpression;

    @ApiModelProperty(name = "foreign key", example = "null")
    private String foreignKey;

}
