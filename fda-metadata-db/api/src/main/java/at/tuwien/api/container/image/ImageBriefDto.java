package at.tuwien.api.container.image;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ImageBriefDto {

    @NotNull
    @ApiModelProperty(required = true, example = "1")
    private Long id;

    @NotBlank
    @ApiModelProperty(required = true, example = "mariadb")
    private String repository;

    @ToString.Exclude
    @ApiModelProperty(required = true, example = "base64:aaaa")
    private String logo;

    @NotBlank
    @ApiModelProperty(required = true, example = "10.5")
    private String tag;

}
