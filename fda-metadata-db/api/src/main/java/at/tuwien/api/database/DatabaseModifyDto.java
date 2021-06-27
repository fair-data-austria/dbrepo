package at.tuwien.api.database;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DatabaseModifyDto {

    @NotNull
    @Parameter(name = "database id", example = "1")
    private Long databaseId;

    @NotBlank
    @Parameter(name = "database name", example = "Weather Australia")
    private String name;

    @NotBlank
    @Parameter(name = "database publicity", example = "true")
    private Boolean isPublic;

}
