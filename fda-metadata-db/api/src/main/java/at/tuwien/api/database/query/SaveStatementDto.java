package at.tuwien.api.database.query;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class SaveStatementDto {

    @NotBlank(message = "statement is required")
    @ApiModelProperty(notes = "sql query")
    private String statement;
}
