package at.tuwien.api.identifier;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Instant;

@Data
@Getter
@Setter
@Builder
public class CreatorDto {

    @NotNull
    private Long id;

    @NotNull
    private Long pid;

    @NotBlank
    @ApiModelProperty(name = "query title", example = "Maximilian")
    private String firstname;

    @NotBlank
    @ApiModelProperty(name = "lastname", example = "Mustermann")
    private String lastname;

    @NotNull
    @ToString.Exclude
    @ApiModelProperty(name = "identifier")
    private IdentifierDto identifier;

    @NotNull
    private Instant created;

    @NotNull
    @JsonProperty("last_modified")
    private Instant lastModified;

}
