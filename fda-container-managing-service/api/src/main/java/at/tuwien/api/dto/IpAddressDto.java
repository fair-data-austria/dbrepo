package at.tuwien.api.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.net.InetAddress;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class IpAddressDto {

    @NotBlank
    @ApiModelProperty(name = "network name", example = "fda-userdb")
    private String network;

    @NotBlank
    @ApiModelProperty(name = "ip address", example = "172.1.2.3")
    private String ipv4;

}
