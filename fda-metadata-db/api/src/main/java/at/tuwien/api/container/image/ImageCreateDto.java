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
public class ImageCreateDto {

    @NotBlank
    @ApiModelProperty(required = true, example = "postgres")
    private String repository;

    @NotBlank
    @ApiModelProperty(required = true, example = "latest")
    private String tag;

    @NotNull
    @ApiModelProperty(required = true, example = "5432")
    private Integer defaultPort;

    @ApiModelProperty(required = true, example = "[{\"key\":\"POSTGRES_USER\",\"value\":\"postgres\"},{\"key\":\"POSTGRES_PASSWORD\",\"value\":\"postgres\"}]")
    private ImageEnvItemDto[] environment;

}
