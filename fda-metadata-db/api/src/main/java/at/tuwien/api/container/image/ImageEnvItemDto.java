package at.tuwien.api.container.image;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ImageEnvItemDto {

    @NotBlank
    @ApiModelProperty(required = true, example = "POSTGRES_USER")
    private String key;

    @NotBlank
    @ApiModelProperty(required = true, example = "postgres")
    private String value;

}
