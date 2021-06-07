package at.tuwien.api.database;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DatabaseCreateDto {

    @NotNull
    @ApiModelProperty(name = "container id", example = "1")
    private Long containerId;

    @NotBlank
    @ApiModelProperty(name = "database name", example = "Weather Australia")
    private String name;

}
