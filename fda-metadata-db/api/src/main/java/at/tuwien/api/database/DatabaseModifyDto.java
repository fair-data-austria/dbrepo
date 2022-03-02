package at.tuwien.api.database;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("is_public")
    @Parameter(name = "database publicity", example = "true")
    private Boolean isPublic;

    @Parameter(name = "database description", example = "Sample")
    private String description;

    @Parameter(name = "database publisher", example = "TU Wien")
    private String publisher;

    @Parameter(name = "database license", example = "MIT")
    private String license;

    @JsonProperty("contact_person")
    @Parameter(name = "database license", example = "Max Mustermann")
    private Long contactPerson;

}
