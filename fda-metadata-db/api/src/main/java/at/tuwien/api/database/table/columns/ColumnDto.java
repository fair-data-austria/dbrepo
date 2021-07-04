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
public class ColumnDto {

    @NotBlank
    @ApiModelProperty(name = "name", example = "Date")
    private String name;

    @NotBlank
    @ApiModelProperty(name = "internal name", example = "mdb_date")
    private String internalName;

    @NotNull
    @ApiModelProperty(name = "primary key", example = "true")
    private Boolean isPrimaryKey;

    @NotNull
    @ApiModelProperty(name = "type", example = "STRING")
    private ColumnTypeDto columnType;

    @NotNull
    @ApiModelProperty(name = "null allowed", example = "true")
    private Boolean isNullAllowed;

    @ApiModelProperty(name = "check constraint", example = "Price Limit > 0", hidden = true)
    private String checkExpression;

    @ApiModelProperty(name = "foreign key", hidden = true)
    private String foreignKey;

}
