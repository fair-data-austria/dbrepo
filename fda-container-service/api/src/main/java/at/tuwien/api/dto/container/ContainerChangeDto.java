package at.tuwien.api.dto.container;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@Builder
@ToString
public class ContainerChangeDto {

    @NotNull
    @ApiModelProperty(required = true, example = "START")
    private ContainerActionTypeDto action;

}
