package at.tuwien.api.container;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@ToString
public class ImageEnvItemDto {

    @NotBlank
    @ApiModelProperty(required = true, example = "POSTGRES_USER")
    private String key;

    @NotBlank
    @ApiModelProperty(required = true, example = "postgres")
    private String value;

}
