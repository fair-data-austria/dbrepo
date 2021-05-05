package at.tuwien.api;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class IpAddressDto {

    @NotNull
    @ApiModelProperty(name = "ip address", example = "172.1.2.3")
    private String ipv4;

}
