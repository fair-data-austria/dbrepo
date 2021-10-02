package at.tuwien.api.database.query;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;


@Data
@Getter
@Setter
@Builder
public class QueryDto {

    private Long id;

    @JsonProperty("execution_timestamp")
    private Timestamp executionTimestamp;

    private String query;

    private String doi;

    @JsonProperty("query_normalized")
    private String queryNormalized;

    @JsonProperty("query_hash")
    private String queryHash;

    @JsonProperty("result_hash")
    private String resultHash;

    @JsonProperty("result_number")
    private Long resultNumber;
}
