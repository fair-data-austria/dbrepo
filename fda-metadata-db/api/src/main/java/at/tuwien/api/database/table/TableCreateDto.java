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

    @NotNull
    @ApiModelProperty(name = "table columns", required = true)
    private ColumnCreateDto[] columns;

}
