package at.tuwien.api.database;

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
public class DatabaseBriefDto {

    @NotNull
    @Min(value = 1)
    @Parameter(name = "database id", example = "1")
    private Long id;

    @NotBlank
    @Parameter(name = "database name", example = "Weather Australia")
    private String name;

    @NotBlank
    @Parameter(name = "database description", example = "Weather in Australia")
    private String description;

    @NotBlank
    @Parameter(name = "database engine", example = "mariadb:latest")
    private String engine;

    @NotBlank
    @Parameter(name = "database creation time", example = "2020-08-04 11:12:00")
    private Instant created;

}
