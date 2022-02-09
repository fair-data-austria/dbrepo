package at.tuwien.api.database;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.*;

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

    @NotNull(message = "database id is required")
    @Parameter(name = "database id", example = "1")
    private Long id;

    @NotBlank(message = "name is required")
    @Parameter(name = "database name", example = "Weather Australia")
    private String name;

    @NotBlank(message = "description is required")
    @Parameter(name = "database description", example = "Weather in Australia")
    private String description;

    @NotBlank(message = "engine is required")
    @Parameter(name = "database engine", example = "mariadb:latest")
    private String engine;

    @Parameter(name = "database creation time", example = "2020-08-04 11:12:00")
    private Instant created;

}
