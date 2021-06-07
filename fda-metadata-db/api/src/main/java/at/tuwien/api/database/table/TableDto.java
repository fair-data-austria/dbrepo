package at.tuwien.api.database.table;

import at.tuwien.api.database.table.columns.RowDto;
import at.tuwien.api.database.table.columns.ColumnDto;
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
public class TableDto {

    @NotNull
    @ApiModelProperty(name = "table id", example = "1")
    private Long id;

    @NotBlank
    @ApiModelProperty(name = "table name", example = "Weather Australia")
    private String name;

    @NotBlank
    @ApiModelProperty(name = "table internal name", example = "weather_australia")
    private String internalName;

    @NotBlank
    @ApiModelProperty(name = "table description", example = "Predict next-day rain in Australia", notes = "https://www.kaggle.com/jsphyg/weather-dataset-rattle-package")
    private String description;

    @NotNull
    @ApiModelProperty(name = "table columns")
    private ColumnDto[] columns;

    @NotNull
    @ApiModelProperty(name = "table rows")
    private RowDto[] rows;

}
