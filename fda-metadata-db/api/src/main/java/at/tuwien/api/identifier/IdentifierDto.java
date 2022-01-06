package at.tuwien.api.identifier;

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
public class IdentifierDto {

    @NotNull
    private Long id;

    @NotNull
    @ApiModelProperty(name = "query id", example = "SQL Query")
    private Long qid;

    @NotBlank
    @ApiModelProperty(name = "query title", example = "Select all weather events for 2012")
    private String title;

    @NotBlank
    @ApiModelProperty(name = "query description", example = "Returns a list of measurements for the year 2012")
    private String description;

    @ApiModelProperty(name = "doi", example = "Digital Object Identifier")
    private String doi;

    @NotNull
    @ApiModelProperty(name = "creators")
    private CreatorDto[] creators;

    @NotNull
    private Instant created;

    @NotNull
    private Instant lastModified;

}
