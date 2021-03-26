package at.tuwien.api.dto.image;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class ImageBriefDto {

    @NotNull
    @ApiModelProperty(required = true, example = "1")
    private Long id;

    @NotNull
    @ApiModelProperty(required = true, example = "postgres")
    private String repository;

    @NotNull
    @ApiModelProperty(required = true, example = "latest")
    private String tag;

}
