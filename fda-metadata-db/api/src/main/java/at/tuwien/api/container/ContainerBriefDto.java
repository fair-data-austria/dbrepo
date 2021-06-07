package at.tuwien.api.container;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ContainerBriefDto {

    @NotNull
    @ApiModelProperty(name = "id", example = "1")
    private Long id;

    @NotNull
    @ApiModelProperty(name = "container hash", example = "f829dd8a884182d0da846f365dee1221fd16610a14c81b8f9f295ff162749e50")
    private String hash;

    @NotBlank
    @ApiModelProperty(name = "container name", example = "Weather World")
    private String name;

    @NotBlank
    @ApiModelProperty(name = "container internal name", example = "weather-world")
    private String internalName;
}
