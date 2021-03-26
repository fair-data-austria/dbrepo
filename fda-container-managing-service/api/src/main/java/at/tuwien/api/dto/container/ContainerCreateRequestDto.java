package at.tuwien.api.dto.container;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
public class ContainerCreateRequestDto {

    @NotBlank
    @ApiModelProperty(name = "name", example = "nyse")
    private String name;

    @NotBlank
    @ApiModelProperty(name = "repository", example = "postgres")
    private String repository;

    @NotBlank
    @ApiModelProperty(name = "tag", example = "latest")
    private String tag = "latest";

}
