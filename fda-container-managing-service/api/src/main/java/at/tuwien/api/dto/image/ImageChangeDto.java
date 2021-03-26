package at.tuwien.api.dto.image;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class ImageChangeDto {

    @Min(value = 1024, message = "only user ports are allowed 1024-65535")
    @Max(value = 65535, message = "only user ports are allowed 1024-65535")
    @ApiModelProperty(example = "5432")
    private Integer defaultPort;

    private ImageEnvItemDto[] environment;

}
