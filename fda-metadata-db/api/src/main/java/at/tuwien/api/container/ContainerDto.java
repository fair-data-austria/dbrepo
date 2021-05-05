package at.tuwien.api.container;

import at.tuwien.api.IpAddressDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.time.Instant;

@Getter
@Setter
@ToString
public class ContainerDto extends ContainerBriefDto {

    @NotNull
    @ApiModelProperty(name = "status", example = "RUNNING")
    private ContainerStateDto status;

    @NotNull
    private IpAddressDto ipAddress;

    @NotNull
    private ImageDto image;

    @NotNull
    private Integer port;

    @NotNull
    @ApiModelProperty(name = "start time", example = "2021-03-12T15:26:21.678396092Z")
    private Instant created;

}
