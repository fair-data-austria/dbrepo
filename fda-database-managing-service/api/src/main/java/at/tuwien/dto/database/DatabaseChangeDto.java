package at.tuwien.dto.database;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class DatabaseChangeDto {

    @NotNull
    private Long databaseId;

}
