package at.tuwien.api.dto.container;

import at.tuwien.api.dto.IpAddressDto;
import at.tuwien.api.dto.image.ImageDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
public class ContainerDto extends ContainerBriefDto {

    @NotNull
    @ApiModelProperty(name = "container state", example = "RUNNING")
    private ContainerStateDto state;

    @NotNull
    private List<IpAddressDto> addresses = new LinkedList<>();

    @NotNull
    private ImageDto image;

    @NotNull
    @ApiModelProperty(name = "actual assigned port", example = "10230")
    private Integer port;

    @NotNull
    @ApiModelProperty(name = "start time", example = "2021-03-12T15:26:21.678396092Z")
    private Instant created;

}
