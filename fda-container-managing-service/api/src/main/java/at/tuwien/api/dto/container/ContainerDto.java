package at.tuwien.api.dto.container;

import lombok.Getter;
import lombok.Setter;

import java.net.InetAddress;
import java.time.Instant;

@Getter
@Setter
public class ContainerDto extends DatabaseContainerBriefDto {

    private ContainerStateDto status;

    private IpAddressDto ipAddress;

    private Instant created;

}
