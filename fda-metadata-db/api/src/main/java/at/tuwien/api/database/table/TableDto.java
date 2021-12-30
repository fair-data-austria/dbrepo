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

    @NotNull
    @ApiModelProperty(name = "deposition id", example = "100")
    private Long depositId;

    @NotBlank
    @ApiModelProperty(name = "table name", example = "Weather Australia")
    private String name;

    @NotBlank
    @ApiModelProperty(name = "table internal name", example = "weather_australia")
    private String internalName;

    @NotBlank
    @ApiModelProperty(name = "topic name", example = "fda.c1.d1.t1")
    private String topic;

    @NotBlank
    @ApiModelProperty(name = "table description", example = "Predict next-day rain in Australia", notes = "https://www.kaggle.com/jsphyg/weather-dataset-rattle-package")
    private String description;

    @NotBlank
    @ApiModelProperty(name = "table csv separator", example = ",")
    private Character separator = ',';

    @NotBlank
    @ApiModelProperty(name = "table csv null element", example = "NA")
    private String nullElement = null;

    @ApiModelProperty(name = "table csv contains a header row", example = "true")
    private Boolean skipHeaders = true;

    @ApiModelProperty(name = "table csv element for boolean true", example = "1")
    private String trueElement = "1";

    @ApiModelProperty(name = "table csv element for boolean false", example = "0")
    private String falseElement = "0";

    @NotNull
    @ApiModelProperty(name = "table columns")
    private ColumnDto[] columns;

    @NotNull
    @ApiModelProperty(name = "table rows")
    private RowDto[] rows;

}
