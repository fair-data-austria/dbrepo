package at.tuwien.api.database.query;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExecuteQueryDto {

    @JsonProperty("Query")
    private String query;

}
