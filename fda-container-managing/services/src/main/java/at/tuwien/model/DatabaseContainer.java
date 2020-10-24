package at.tuwien.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@NoArgsConstructor
@Getter
@Setter
public class DatabaseContainer {

    @JsonProperty("ContainerID")
    private String containerID;
    @JsonProperty("Created")
    private String created;
    @JsonProperty("ContainerName")
    private String containerName;
    @JsonProperty("DbName")
    private String dbName;
    @JsonProperty("Status")
    private String status;
    @JsonProperty("IpAddress")
    private String ipAddress;

}
