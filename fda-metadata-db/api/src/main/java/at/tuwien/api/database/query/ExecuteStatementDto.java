package at.tuwien.api.database.query;

import at.tuwien.api.database.table.TableBriefDto;
import at.tuwien.api.database.table.columns.ColumnBriefDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExecuteStatementDto {

    @NotBlank
    @ApiModelProperty(name = "sql query")
    private String statement;

    @NotNull
    @ApiModelProperty(name = "tables mentioned in the query")
    private List<TableBriefDto> tables;

    @NotNull
    @ApiModelProperty(name = "columns mentioned in the query")
    private List<ColumnBriefDto> columns;
}
