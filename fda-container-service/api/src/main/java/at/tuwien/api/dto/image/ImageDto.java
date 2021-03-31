package at.tuwien.api.dto.image;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Immutable;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.time.Instant;
import java.util.Collection;

@Getter
@Setter
@ToString
public class ImageDto {

    @NotBlank
    @ApiModelProperty(required = true, example = "postgres")
    private String repository;

    @NotBlank
    @ApiModelProperty(required = true, example = "latest")
    private String tag;

    @NotBlank
    @ApiModelProperty(required = true, example = "sha256:c5ec7353d87dfc35067e7bffeb25d6a0d52dad41e8b7357213e3b12d6e7ff78e")
    private String hash;

    @NotBlank
    @ApiModelProperty(required = true, example = "2021-03-12T15:26:21.678396092Z")
    private String compiled;

    @NotNull
    @ApiModelProperty(required = true, example = "314295447")
    private BigInteger size;

    @NotNull
    @ApiModelProperty(required = true, example = "5432")
    private Integer defaultPort;

    private ImageEnvItemDto[] environment;

}