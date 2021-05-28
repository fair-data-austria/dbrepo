package at.tuwien.api.container.network;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@Builder
public class IpAddressDto {

    @NotNull
    @ApiModelProperty(name = "ip address", example = "172.1.2.3")
    private String ipv4;

}
