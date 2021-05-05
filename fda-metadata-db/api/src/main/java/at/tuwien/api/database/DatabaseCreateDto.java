package at.tuwien.api.database;

import io.swagger.annotations.ApiModelProperty;
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
    @ApiModelProperty(name = "at.tuwien.container id", example = "1")
    private Long containerId;

    @NotBlank
    @ApiModelProperty(name = "at.tuwien.database name", example = "Exchange Traded Fund")
    private String name;

}
