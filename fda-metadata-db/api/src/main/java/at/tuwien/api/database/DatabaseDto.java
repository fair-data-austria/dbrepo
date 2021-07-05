package at.tuwien.api.database;

import at.tuwien.api.container.image.ImageDto;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DatabaseDto {

    @NotNull
    @Min(value = 1)
    @Parameter(name = "database id", example = "1")
    private Long id;

    @NotBlank
    @Parameter(name = "database name", example = "Weather Australia")
    private String name;

    @NotBlank
    @Parameter(name = "database internal name", example = "weather_australia")
    private String internalName;

    @NotBlank
    @Parameter(name = "database description", example = "Weather Australia 2009-2021")
    private String description;

    @NotBlank
    @Parameter(name = "database container image")
    private ImageDto image;

    @NotBlank
    @Parameter(name = "database publisher", example = "National Office")
    private String publisher;

}
