package at.tuwien.api.container;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ContainerCreateRequestDto {

    @NotBlank
    @ApiModelProperty(name = "name", example = "Weather World")
    private String name;

    @NotBlank
    @ApiModelProperty(name = "repository", example = "postgres")
    private String repository;

    @NotBlank
    @ApiModelProperty(name = "tag", example = "latest")
    private String tag = "latest";

}
