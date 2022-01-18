package at.tuwien.api.database.table;

import at.tuwien.api.database.table.columns.ColumnCreateDto;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

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

    @NotBlank
    @ApiModelProperty(name = "table topic", required = true, example = "Weather Data")
    private String topic;


    @NotNull
    @ApiModelProperty(name = "table columns", required = true, example = "[{\"checkExpression\":null,\"foreignKey\":null,\"name\":\"id\",\"nullAllowed\":false,\"primaryKey\":true,\"type\":\"NUMBER\",\"unique\":true,\"enumValues\":[]},{\"checkExpression\":null,\"foreignKey\":null,\"name\":\"Date\",\"nullAllowed\":true,\"primaryKey\":false,\"type\":\"DATE\",\"unique\":false,\"enumValues\":[]},{\"checkExpression\":null,\"foreignKey\":null,\"name\":\"Location\",\"nullAllowed\":true,\"primaryKey\":false,\"type\":\"STRING\",\"unique\":false,\"enumValues\":[]},{\"checkExpression\":null,\"foreignKey\":null,\"name\":\"MinTemp\",\"nullAllowed\":true,\"primaryKey\":false,\"type\":\"NUMBER\",\"unique\":false,\"enumValues\":[]},{\"checkExpression\":null,\"foreignKey\":null,\"name\":\"MaxTemp\",\"nullAllowed\":true,\"primaryKey\":false,\"type\":\"NUMBER\",\"unique\":false,\"enumValues\":[]},{\"checkExpression\":null,\"foreignKey\":null,\"name\":\"Rainfall\",\"nullAllowed\":true,\"primaryKey\":false,\"type\":\"NUMBER\",\"unique\":false,\"enumValues\":[]}]")
    private ColumnCreateDto[] columns;

}
