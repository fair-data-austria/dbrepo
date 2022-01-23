package at.tuwien.api.database.table.columns;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("primary_key")
    @ApiModelProperty(name = "primary key", example = "true")
    private Boolean primaryKey = false;

    @NotNull
    @ApiModelProperty(name = "column type", example = "STRING")
    private ColumnTypeDto type;

    @NotNull
    @JsonProperty("null_allowed")
    @ApiModelProperty(name = "null value", example = "false")
    private Boolean nullAllowed = true;

    @JsonProperty("date_format")
    @ApiModelProperty(name = "date format", example = "YYYY-mm-dd")
    private String dateFormat;

    @NotNull
    @ApiModelProperty(name = "unique", example = "true")
    private Boolean unique = false;

    @JsonProperty("check_expression")
    @ApiModelProperty(name = "check expression", example = "null")
    private String checkExpression = null;

    @JsonProperty("foreign_key")
    @ApiModelProperty(name = "foreign key", example = "null")
    private String foreignKey = null;

    @ApiModelProperty(name = "foreign key reference, only considered when foreignKey != null", example = "null")
    private String references = null;

    @JsonProperty("enum_values")
    @ApiModelProperty(name = "enum values, only considered when type = ENUM", example = "[\"male\",\"female\",\"other\"]")
    private String[] enumValues = null;

}
