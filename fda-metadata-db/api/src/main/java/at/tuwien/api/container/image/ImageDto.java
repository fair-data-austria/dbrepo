package at.tuwien.api.container.image;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.time.Instant;
import java.util.List;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ImageDto {

    @NotNull
    @ApiModelProperty(required = true, example = "1")
    private Long id;

    @NotBlank
    @ApiModelProperty(required = true, example = "mariadb")
    private String repository;

    @NotBlank
    @ApiModelProperty(required = true, example = "10.5")
    private String tag;

    @NotBlank
    @JsonProperty("driver_class")
    @ApiModelProperty(required = true, example = "org.postgresql.Driver")
    private String driverClass;

    @ToString.Exclude
    @ApiModelProperty(required = true)
    private String logo;

    @JsonProperty("date_formats")
    private List<ImageDateDto> dateFormats;

    @NotBlank
    @ApiModelProperty(required = true, example = "Postgres")
    private String dialect;

    @NotBlank
    @JsonProperty("jdbc_method")
    @ApiModelProperty(required = true, example = "postgres")
    private String jdbcMethod;

    @ApiModelProperty(required = true, example = "sha256:c5ec7353d87dfc35067e7bffeb25d6a0d52dad41e8b7357213e3b12d6e7ff78e")
    private String hash;

    @ApiModelProperty(required = true, example = "2021-03-12T15:26:21.678396092Z")
    private Instant compiled;

    @ApiModelProperty(required = true, example = "314295447")
    private BigInteger size;

    @NotNull
    @JsonProperty("default_port")
    @ApiModelProperty(required = true, example = "5432")
    private Integer defaultPort;

    @NotNull
    @ApiModelProperty(required = true)
    private ImageEnvItemDto[] environment;

}
