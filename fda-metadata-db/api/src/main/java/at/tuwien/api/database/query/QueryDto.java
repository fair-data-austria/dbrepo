package at.tuwien.api.database.query;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Instant;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class QueryDto {

    @NotNull
    @ApiModelProperty(name = "query id", example = "1")
    private Long id;

    @NotNull
    @ApiModelProperty(name = "container id", example = "1")
    private Long cid;

    @NotNull
    @ApiModelProperty(name = "database id", example = "1")
    private Long dbid;

    @ApiModelProperty(name = "execution time", example = "2022-01-01 08:00:00.000")
    private Instant execution;

    @NotBlank
    @ApiModelProperty(name = "query raw", example = "select * from table")
    private String query;

    @JsonProperty("query_normalized")
    @ApiModelProperty(name = "query normalized", example = "select id, name from table")
    private String queryNormalized;

    @NotBlank
    @JsonProperty("query_hash")
    @ApiModelProperty(name = "query hash sha256", example = "17e682f060b5f8e47ea04c5c4855908b0a5ad612022260fe50e11ecb0cc0ab76")
    private String queryHash;

    @JsonProperty("result_hash")
    @ApiModelProperty(name = "result hash sha256", example = "17e682f060b5f8e47ea04c5c4855908b0a5ad612022260fe50e11ecb0cc0ab76")
    private String resultHash;

    @JsonProperty("result_number")
    @ApiModelProperty(name = "result number of records", example = "1")
    private Long resultNumber;

    @NotNull
    private Instant created;

    @NotNull
    @JsonProperty("last_modified")
    private Instant lastModified;

}
