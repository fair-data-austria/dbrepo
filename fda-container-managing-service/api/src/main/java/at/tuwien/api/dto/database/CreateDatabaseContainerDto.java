package at.tuwien.api.dto.database;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateDatabaseContainerDto {

    private String containerName;

    private String databaseName;

}
