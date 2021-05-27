package at.tuwien.api.database.query;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExecuteStatementDto {

    @JsonProperty("Statement")
    private String statement;
}
