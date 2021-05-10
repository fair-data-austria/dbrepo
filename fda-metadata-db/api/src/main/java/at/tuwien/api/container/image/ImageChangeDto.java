package at.tuwien.api.container.image;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ImageChangeDto {

    @Min(value = 1024, message = "only user ports are allowed 1024-65535")
    @Max(value = 65535, message = "only user ports are allowed 1024-65535")
    @ApiModelProperty(example = "5432")
    private Integer defaultPort;

    private ImageEnvItemDto[] environment;

}
