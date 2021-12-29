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
    private Boolean primaryKey = false;

    @NotNull
    @ApiModelProperty(name = "column type", example = "STRING")
    private ColumnTypeDto type;

    @NotNull
    @ApiModelProperty(name = "null value", example = "false")
    private Boolean nullAllowed = true;

    @NotBlank
    @ApiModelProperty(name = "date format", example = "YYYY-mm-dd")
    private String dateFormat;

    @NotNull
    @ApiModelProperty(name = "unique", example = "true")
    private Boolean unique = false;

    @ApiModelProperty(name = "check expression", example = "null")
    private String checkExpression = null;

    @ApiModelProperty(name = "foreign key", example = "null")
    private String foreignKey = null;

    @ApiModelProperty(name = "foreign key reference, only considered when foreignKey != null", example = "null")
    private String references = null;

    @ApiModelProperty(name = "enum values, only considered when type = ENUM", example = "[\"male\",\"female\",\"other\"]")
    private String[] enumValues = null;

}
