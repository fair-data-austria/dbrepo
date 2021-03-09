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

    private String containerID;

    private String created;

    private String containerName;

    private String dbName;

    private String status;

    private String ipAddress;

}
