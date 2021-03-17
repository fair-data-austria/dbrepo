package at.tuwien.dto.database;

import at.tuwien.dto.table.TableDto;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.Instant;

@Getter
@Setter
public class DatabaseCreateDto {

    @NotBlank
    @Parameter(name = "container id", description = "container hash")
    private String containerId;

    @NotBlank
    @Size(min = 3)
    @Parameter(name = "database name")
    private String name;

    @NotBlank
    @Parameter(name = "database engine", example = "postgres")
    private String engine;

    @NotBlank
    @Size(min = 3)
    @Parameter(name = "database owner")
    private String owner;

    @NotBlank
    @Size(min = 3)
    @Parameter(name = "database creator name")
    private String creator;

    @NotBlank
    @Size(min = 3)
    @Parameter(name = "database publisher name")
    private String publisher;

    @NotBlank
    @Parameter(name = "database publication year", example = "2021")
    private Instant publicationYear;

    @NotBlank
    @Parameter(name = "database resource type")
    private String ResourceType;

    @Parameter(name = "database description")
    private String description;

    @NotBlank
    @Parameter(name = "database table definition scheme")
    private TableDto[] tables;

}
