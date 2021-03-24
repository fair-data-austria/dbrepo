package at.tuwien.api.dto.container;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class ContainerChangeDto {

    @NotNull
    private ContainerActionTypeDto action;

}
