package at.tuwien.api.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.net.InetAddress;

@Getter
@Setter
public class IpAddressDto {

    @NotNull
    @ApiModelProperty(name = "ip address", example = "172.1.2.3")
    private String ipv4;

}
