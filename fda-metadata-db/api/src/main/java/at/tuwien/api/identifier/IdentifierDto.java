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

    @NotBlank
    @ApiModelProperty(name = "query title", example = "Select all weather events for 2012")
    private String title;

    @NotBlank
    @ApiModelProperty(name = "query description", example = "Returns a list of measurements for the year 2012")
    private String description;

    @ApiModelProperty(name = "doi", example = "Digital Object Identifier")
    private String doi;

    @NotBlank
    @ApiModelProperty(name = "query", example = "SQL Query")
    private String query;

    @NotNull
    @ApiModelProperty(name = "creators")
    private IdentifierDto[] creators;

    @NotNull
    private Instant created;

    @NotNull
    private Instant lastModified;

}
