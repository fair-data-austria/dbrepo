package at.tuwien.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateDatabaseContainerDTO {

    @JsonProperty("ContainerName")
    private String containerName;
    @JsonProperty("DatabaseName")
    private String dbName;
    @JsonProperty("MasterUser")
    private String masterUser;
    @JsonProperty("Password")
    private String password;

}
