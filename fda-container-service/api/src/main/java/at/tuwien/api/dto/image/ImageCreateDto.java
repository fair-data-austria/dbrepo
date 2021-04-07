package at.tuwien.api.dto.image;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
public class ImageCreateDto {

    @NotBlank
    @ApiModelProperty(required = true, example = "postgres")
    private String repository;

    @NotBlank
    @ApiModelProperty(required = true, example = "latest")
    private String tag;

    @NotBlank
    @ApiModelProperty(required = true, example = "false", notes = "when false, the service pulls it from hub.docker.com")
    private Boolean local;

    @NotNull
    @ApiModelProperty(required = true, example = "5432")
    private Integer defaultPort;

    @ApiModelProperty(required = true, example = "[{\"key\":\"POSTGRES_USER\",\"value\":\"postgres\"},{\"key\":\"POSTGRES_PASSWORD\",\"value\":\"postgres\"}]")
    private ImageEnvItemDto[] environment;

}
