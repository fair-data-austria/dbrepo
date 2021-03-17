package at.tuwien.dto.database;

import at.tuwien.dto.table.TableDto;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class DatabaseCreateDto {

    @Parameter(name = "container id", description = "container hash")
    private String containerId;

    @Parameter(name = "database name")
    private String name;

    @Parameter(name = "database engine")
    private String engine;

    @Parameter(name = "database owner")
    private String owner;

    @Parameter(name = "database creator name")
    private String creator;

    @Parameter(name = "database publisher name")
    private String publisher;

    @Parameter(name = "database publication year")
    private Instant publicationYear;

    @Parameter(name = "database resource type")
    private String ResourceType;

    @Parameter(name = "database description")
    private String description;

    @Parameter(name = "database table definition scheme")
    private TableDto[] tables;

}
