package at.tuwien.entities.database.query;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QueryResult {

    @JsonProperty("Result")
    private List<Map<String, Object>> result;

}
