package at.tuwien.api.container.image;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ImageEnvItemDto {

    @NotNull
    @ApiModelProperty(required = true, example = "1")
    private Long iid;

    @NotBlank
    @ApiModelProperty(required = true, example = "POSTGRES_USER")
    private String key;

    @NotBlank
    @ApiModelProperty(required = true, example = "postgres")
    private String value;

    @NonNull
    @ApiModelProperty(required = true, example = "USERNAME")
    private ImageEnvItemTypeDto type;

}
