package at.tuwien.api.database.query;

import at.tuwien.api.database.deposit.files.FileDto;
import at.tuwien.api.database.table.TableDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;


@Data
@Getter
@Setter
@Builder
public class QueryDto {

    @NotNull
    private Long id;

    @NotNull
    private Long qdbid;

    @JsonProperty("execution_timestamp")
    private Instant executionTimestamp;

    @NotBlank
    @ApiModelProperty(name = "query raw", example = "select * from table")
    private String query;

    @NotBlank
    @ApiModelProperty(name = "query title", example = "Select all weather events for 2012")
    private String title;

    @NotBlank
    @ApiModelProperty(name = "query description", example = "Returns a list of measurements for the year 2012")
    private String description;

    @ApiModelProperty(name = "doi", example = "Digital Object Identifier")
    private String doi;

    @JsonProperty("deposit_id")
    private Long depositId;

    private List<FileDto> files;

    private TableDto table;

    @JsonProperty("query_normalized")
    @ApiModelProperty(name = "query normalized", example = "select id, name from table")
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
    private Instant lastModified;

    /**
     * Returns the ordered list of prepared values for the {@link org.hibernate.query.NativeQuery}.
     *
     * @return The ordered list of prepared values
     */
    public String[] getPreparedValues() {
        return new String[]{
                this.getDoi(),
                this.getTitle(),
                this.getDescription(),
                this.getQuery(),
                this.getQueryHash(),
                Timestamp.from(this.getExecutionTimestamp())
                        .toString(),
                this.getResultHash(),
                String.valueOf(this.getResultNumber())
        };
    }
}
