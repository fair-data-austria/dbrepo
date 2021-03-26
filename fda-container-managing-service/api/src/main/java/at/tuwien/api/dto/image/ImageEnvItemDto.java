package at.tuwien.api.dto.image;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class ImageEnvItemDto {

    @NotBlank
    @ApiModelProperty(required = true, example = "POSTGRES_USER")
    private String key;

    @NotBlank
    @ApiModelProperty(required = true, example = "postgres")
    private String value;

}
