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
    @ApiModelProperty(name = "at.tuwien.container id", example = "1")
    private Long containerId;

    @NotBlank
    @ApiModelProperty(name = "at.tuwien.database name", example = "Exchange Traded Fund")
    private String name;

}
