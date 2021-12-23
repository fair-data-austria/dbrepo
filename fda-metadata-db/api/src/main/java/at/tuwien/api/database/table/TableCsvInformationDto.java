package at.tuwien.api.database.table;

import at.tuwien.api.database.table.columns.ColumnTypeDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TableCsvInformationDto {

    @NotBlank
    @ApiModelProperty(name = "name", example = "Fundamentals")
    private String name;

    @NotBlank
    @ApiModelProperty(name = "table description", required = true, example = "SEC 10K annual fillings (2016-2012) ")
    private String description;

    @NotNull
    private List<ColumnTypeDto> columns;

    @NotBlank
    private String fileLocation;

}
