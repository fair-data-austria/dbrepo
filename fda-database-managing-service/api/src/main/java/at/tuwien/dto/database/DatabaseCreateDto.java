package at.tuwien.dto.database;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class DatabaseCreateDto {

    @NotBlank
    @Parameter(name = "container id", description = "container hash", example = "1")
    private Long containerId;

    @NotBlank
    @Parameter(name = "database name", example = "CTFs")
    private String name;

}
