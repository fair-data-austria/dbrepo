package at.tuwien.dto.container;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class ContainerDto {

    private String containerId;

    private String created;

    private String containerName;

    private String databaseName;

    private String status;

    private String ipAddress;

}
