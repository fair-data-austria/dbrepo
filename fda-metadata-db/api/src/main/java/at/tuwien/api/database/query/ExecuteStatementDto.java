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
@ToString
public class ExecuteStatementDto {

    @NotBlank(message = "statement is required")
    @ApiModelProperty(name = "sql query")
    private String statement;

    @NotNull(message = "list of tables is required")
    @ApiModelProperty(name = "tables mentioned in the query")
    private List<TableBriefDto> tables;

    @NotNull(message = "list of columns is required")
    @ApiModelProperty(name = "columns mentioned in the query")
    private List<List<ColumnBriefDto>> columns;
}
