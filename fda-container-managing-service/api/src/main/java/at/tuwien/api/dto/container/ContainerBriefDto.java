package at.tuwien.api.dto.container;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
public class ContainerBriefDto {

    @NotNull
    @Size(min = 64, max = 64)
    private String id;

    @NotBlank
    @Size(min = 3)
    private String containerName;
}
