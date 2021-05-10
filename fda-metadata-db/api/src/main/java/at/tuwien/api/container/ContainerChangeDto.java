package at.tuwien.api.container;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ContainerChangeDto {

    @NotNull
    @ApiModelProperty(required = true, example = "START")
    private ContainerActionTypeDto action;

}
