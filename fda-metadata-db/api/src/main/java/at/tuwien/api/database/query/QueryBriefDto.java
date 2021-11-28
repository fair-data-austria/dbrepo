package at.tuwien.api.database.query;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Instant;


@Data
@Getter
@Setter
@Builder
public class QueryBriefDto {

    @NotNull
    private Long id;

    @ApiModelProperty(name = "query execution time", example = "2021-11-28T12:00:01.000")
    private Instant executionTimestamp;

    @NotBlank
    @ApiModelProperty(name = "query hash", example = "sha256:62e2f583cee1e7879eb32afa191a05f2f7ca8af5c4218997638553b85f94389c")
    private String queryHash;

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

    @NotNull
    private Instant created;

    @NotNull
    private Instant lastModified;
}
