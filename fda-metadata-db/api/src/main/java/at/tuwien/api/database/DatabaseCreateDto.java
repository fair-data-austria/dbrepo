package at.tuwien.api.database;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DatabaseCreateDto {

    @NotBlank
    @ApiModelProperty(name = "database name", example = "Weather Australia")
    private String name;

    @NotNull
    @JsonProperty("is_public")
    @Parameter(name = "database publicity", example = "true")
    private Boolean isPublic;

    @NotBlank
    @Parameter(name = "database description", example = "true")
    private String description;

}
