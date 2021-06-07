package at.tuwien.api.database.table;

import at.tuwien.api.database.table.columns.ColumnCreateDto;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TableCreateDto {

    @NotBlank
    @ApiModelProperty(name = "name", example = "Weather Australia")
    private String name;

    @NotBlank
    @ApiModelProperty(name = "table description", required = true, example = "Predict next-day rain in Australia", notes = "https://www.kaggle.com/jsphyg/weather-dataset-rattle-package")
    private String description;

    @NotNull
    @ApiModelProperty(name = "table columns", required = true, example = "[{\"checkExpression\":null,\"foreignKey\":null,\"name\":\"id\",\"nullAllowed\":false,\"primaryKey\":true,\"type\":\"NUMBER\",\"unique\":true},{\"checkExpression\":null,\"foreignKey\":null,\"name\":\"Date\",\"nullAllowed\":false,\"primaryKey\":false,\"type\":\"DATE\",\"unique\":false},{\"checkExpression\":null,\"foreignKey\":null,\"name\":\"Location\",\"nullAllowed\":false,\"primaryKey\":false,\"type\":\"STRING\",\"unique\":false},{\"checkExpression\":null,\"foreignKey\":null,\"name\":\"MinTemp\",\"nullAllowed\":false,\"primaryKey\":false,\"type\":\"NUMBER\",\"unique\":false},{\"checkExpression\":null,\"foreignKey\":null,\"name\":\"MaxTemp\",\"nullAllowed\":false,\"primaryKey\":false,\"type\":\"NUMBER\",\"unique\":false},{\"checkExpression\":null,\"foreignKey\":null,\"name\":\"Rainfall\",\"nullAllowed\":false,\"primaryKey\":false,\"type\":\"NUMBER\",\"unique\":false}]")
    private ColumnCreateDto[] columns;

}
