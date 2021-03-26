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
    @Parameter(name = "database name")
    private String name;

    @NotBlank
    @Parameter(name = "database engine", example = "postgres")
    private String engine;

}
