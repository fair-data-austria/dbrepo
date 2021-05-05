package at.tuwien.api.database;

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
