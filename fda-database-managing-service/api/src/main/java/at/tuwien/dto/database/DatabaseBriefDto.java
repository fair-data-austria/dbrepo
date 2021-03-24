package at.tuwien.dto.database;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
public class DatabaseBriefDto {

    @NotNull
    @Size(min = 64, max = 64)
    private Long id;

}
