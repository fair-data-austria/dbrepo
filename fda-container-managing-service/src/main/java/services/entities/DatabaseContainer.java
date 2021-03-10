package services.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


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
