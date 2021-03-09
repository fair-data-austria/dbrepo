package at.tuwien.dto.database;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CreateDatabaseContainerDto {

    @JsonProperty("container_name")
    private String containerName;

    @JsonProperty("database_name")
    private String databaseName;

}
