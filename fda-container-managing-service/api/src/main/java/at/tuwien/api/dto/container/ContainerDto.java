package at.tuwien.api.dto.container;

import at.tuwien.api.dto.IpAddressDto;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.Instant;

@Getter
@Setter
public class ContainerDto extends ContainerBriefDto {

    @NotNull
    private ContainerStateDto status;

    @NotNull
    private IpAddressDto ipAddress;

    @NotNull
    private Instant created;

}
