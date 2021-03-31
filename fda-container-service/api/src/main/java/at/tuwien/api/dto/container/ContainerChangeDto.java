package at.tuwien.api.dto.container;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
public class ContainerChangeDto {

    @NotNull
    @ApiModelProperty(required = true, example = "START")
    private ContainerActionTypeDto action;

}