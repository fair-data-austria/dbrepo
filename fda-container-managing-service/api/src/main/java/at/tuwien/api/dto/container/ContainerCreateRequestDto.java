package at.tuwien.api.dto.container;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ContainerCreateRequestDto {

    @NotBlank
    @ApiModelProperty(name = "name", example = "New York Stock Exchange")
    private String name;

    @NotBlank
    @ApiModelProperty(name = "repository", example = "postgres")
    private String repository;

    @NotBlank
    @ApiModelProperty(name = "tag", example = "latest")
    private String tag = "latest";

}
