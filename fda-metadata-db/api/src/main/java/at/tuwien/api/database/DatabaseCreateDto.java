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

    @NotBlank(message = "database name is required")
    @ApiModelProperty(name = "database name", example = "Weather Australia")
    private String name;

    @NotNull(message = "public attribute is required")
    @JsonProperty("is_public")
    @Parameter(name = "database publicity", example = "true")
    private Boolean isPublic;

    @NotBlank(message = "description is required")
    @Parameter(name = "database description", example = "true")
    private String description;

}
