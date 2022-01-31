package at.tuwien.api.container.image;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

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

    @NotBlank
    @JsonProperty("driver_class")
    @ApiModelProperty(required = true, example = "org.postgresql.Driver")
    private String driverClass;

    @NotBlank
    @ApiModelProperty(required = true, example = "base64:aaaa")
    private String logo;

    @NotBlank
    @ApiModelProperty(required = true, example = "Postgres")
    private String dialect;

    @NotBlank
    @JsonProperty("jdbc_method")
    @ApiModelProperty(required = true, example = "postgresql")
    private String jdbcMethod;

}
