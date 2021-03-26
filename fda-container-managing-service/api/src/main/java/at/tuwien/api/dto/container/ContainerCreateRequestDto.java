package at.tuwien.api.dto.container;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
public class ContainerCreateRequestDto {

    @NotBlank
    private String name;

    @NotBlank
    private String repository;

    @NotBlank
    private String tag = "latest";

}
