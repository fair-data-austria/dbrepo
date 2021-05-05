package at.tuwien.dto.database;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
public class DatabaseCreateDto {

    @NotNull
    @ApiModelProperty(name = "container id", example = "1")
    private Long containerId;

    @NotBlank
    @ApiModelProperty(name = "database name", example = "Exchange Traded Fund")
    private String name;

}
