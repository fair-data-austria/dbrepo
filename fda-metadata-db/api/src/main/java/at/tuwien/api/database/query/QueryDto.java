package at.tuwien.api.database.query;

import at.tuwien.api.database.deposit.files.FileDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.time.Instant;


@Data
@Getter
@Setter
@Builder
public class QueryDto {

    @NotNull
    private Long id;

    @JsonProperty("execution_timestamp")
    private Instant executionTimestamp;

    @NotBlank
    private String query;

    @NotBlank
    private String title;

    private String doi;

    @JsonProperty("deposit_id")
    private Long depositId;

    private FileDto file;

    @JsonProperty("query_normalized")
    private String queryNormalized;

    @JsonProperty("query_hash")
    private String queryHash;

    @JsonProperty("result_hash")
    private String resultHash;

    @JsonProperty("result_number")
    private Long resultNumber;

    @NotNull
    private Instant created;

    @NotNull
    private Instant modified;
}
