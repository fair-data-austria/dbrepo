package at.tuwien.api.dto.image;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class ImageEnvItemDto {

    @NotNull
    @ApiModelProperty(required = true, example = "POSTGRES_USER")
    private String key;

    @NotNull
    @ApiModelProperty(required = true, example = "postgres")
    private String value;

}
