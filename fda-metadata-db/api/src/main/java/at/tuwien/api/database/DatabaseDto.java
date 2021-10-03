package at.tuwien.api.database;

import at.tuwien.api.container.ContainerDto;
import at.tuwien.api.container.image.ImageDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Instant;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DatabaseDto {

    @NotNull
    @Min(value = 1)
    @ApiModelProperty(name = "database id", example = "1")
    private Long id;

    @NotBlank
    @ApiModelProperty(name = "database name", example = "Weather Australia")
    private String name;

    @NotBlank
    @JsonProperty("internal_name")
    @ApiModelProperty(name = "database internal name", example = "weather_australia")
    private String internalName;

    @NotBlank
    @ApiModelProperty(name = "database description", example = "Weather Australia 2009-2021")
    private String description;

    @NotBlank
    @ApiModelProperty(name = "database exchange", example = "fda.c1.d1")
    private String exchange;

    @NotBlank
    @ApiModelProperty(name = "database container image")
    private ImageDto image;

    @NotBlank
    @ApiModelProperty(name = "container")
    private ContainerDto container;

    @NotBlank
    @ApiModelProperty(name = "database publisher", example = "National Office")
    private String publisher;

    @NotBlank
    @ApiModelProperty(name = "database creation time", example = "2020-08-04 11:12:00")
    private Instant created;

    @NotBlank
    @ApiModelProperty(name = "database deletion time", example = "2020-08-04 11:13:00")
    private Instant deleted;

}
