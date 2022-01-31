package at.tuwien.api.database.table;

import at.tuwien.api.database.table.columns.ColumnDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Instant;

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
    @JsonProperty("internal_name")
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
    @JsonProperty("null_element")
    @ApiModelProperty(name = "table csv null element", example = "NA")
    private String nullElement = null;

    @JsonProperty("skip_lines")
    @ApiModelProperty(name = "table csv contains a header row", example = "0")
    private Long skipLines = 0L;

    @JsonProperty("true_element")
    @ApiModelProperty(name = "table csv element for boolean true", example = "1")
    private String trueElement = "1";

    @JsonProperty("false_element")
    @ApiModelProperty(name = "table csv element for boolean false", example = "0")
    private String falseElement = "0";

    @ApiModelProperty(name = "table creation time")
    private Instant created;

    @NotNull
    @ApiModelProperty(name = "table columns")
    private ColumnDto[] columns;

}
