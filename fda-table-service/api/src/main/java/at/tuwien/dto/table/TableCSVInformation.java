package at.tuwien.dto.table;

import at.tuwien.dto.table.columns.ColumnTypeDto;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.*;

import javax.validation.constraints.NotBlank;
import java.util.HashMap;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class TableCSVInformation {

    @NotBlank
    @ApiModelProperty(name = "name", example = "Fundamentals")
    private String name;

    @NotBlank
    @Parameter(name = "table description", required = true, example = "SEC 10K annual fillings (2016-2012) ")
    private String description;

    @NotBlank
    private List<ColumnTypeDto> columns;

    private String fileLocation;
}
