package at.tuwien.dto.database;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
public class DatabaseDto extends DatabaseBriefDto {

    @NotBlank
    @Size(min = 3)
    private String name;

    @NotBlank
    @Size(min = 64, max = 64)
    private String containerId;

}
