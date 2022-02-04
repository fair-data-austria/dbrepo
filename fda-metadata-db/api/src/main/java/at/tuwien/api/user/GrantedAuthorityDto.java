package at.tuwien.api.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GrantedAuthorityDto {

    @ApiModelProperty(name = "authority name")
    private String authority;



}
