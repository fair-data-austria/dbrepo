package at.tuwien.api.database.query;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExecuteStatementDto {

    @NotBlank
    @ApiModelProperty(notes = "sql query")
    private String statement;
}
