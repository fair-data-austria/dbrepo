package at.tuwien.dto.database;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
public class DatabaseChangeDto {

    @NotNull
    private Long databaseId;

}
