package at.tuwien.api.container.image;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.persistence.Column;
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

    @NotBlank
    @JsonProperty("driver_class")
    @ApiModelProperty(required = true, example = "org.postgresql.Driver")
    private String driverClass;

    @NotBlank
    @ApiModelProperty(required = true, example = "POSTGRES")
    private String dialect;

    @NotBlank
    @ApiModelProperty(required = true, example = "base64:aaaa")
    private String logo;

    @NotBlank
    @JsonProperty("jdbc_method")
    @ApiModelProperty(required = true, example = "postgresql")
    private String jdbcMethod;

    @NotNull
    @ApiModelProperty(required = true, example = "false", notes = "when false, the service pulls it from hub.docker.com")
    private Boolean local;

    @NotNull
    @JsonProperty("default_port")
    @ApiModelProperty(required = true, example = "5432")
    private Integer defaultPort;

    @ApiModelProperty(required = true, example = "[{\"key\":\"POSTGRES_USER\",\"value\":\"postgres\",\"type\":USERNAME},{\"key\":\"POSTGRES_PASSWORD\",\"value\":\"postgres\",\"type\":PASSWORD}]")
    private ImageEnvItemDto[] environment;

}
