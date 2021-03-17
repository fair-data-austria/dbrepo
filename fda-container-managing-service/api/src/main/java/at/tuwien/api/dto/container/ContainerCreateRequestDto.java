package at.tuwien.api.dto.container;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@Builder
public class ContainerCreateRequestDto {

    @NotBlank
    @Size(min = 3)
    private String containerName;

    @NotBlank
    @Size(min = 3)
    private String image;

}
