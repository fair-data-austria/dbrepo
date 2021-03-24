package at.tuwien.api.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.net.InetAddress;

@Getter
@Setter
public class IpAddressDto {

    @NotNull
    @Size(min = 7, max = 15)
    private String ipv4;

}
