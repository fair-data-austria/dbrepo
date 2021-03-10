package at.tuwien.api.dto.container;

import lombok.Getter;
import lombok.Setter;

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
