package at.tuwien.api.container;

import at.tuwien.api.container.image.ImageDto;
import at.tuwien.api.database.DatabaseDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.List;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ContainerDto {

    @NotNull
    @ApiModelProperty(name = "id", example = "1")
    private Long id;

    @NotNull
    @ApiModelProperty(name = "container hash", example = "f829dd8a884182d0da846f365dee1221fd16610a14c81b8f9f295ff162749e50")
    private String hash;

    @NotBlank
    @ApiModelProperty(name = "container name", example = "Weather World")
    private String name;

    @NotBlank
    @JsonProperty("internal_name")
    @ApiModelProperty(name = "container internal name", example = "weather-world")
    private String internalName;

    @NotNull
    @ApiModelProperty(name = "state", example = "RUNNING")
    private ContainerStateDto state;

    @NotNull
    @ToString.Exclude
    @ApiModelProperty(name = "databases")
    private List<DatabaseDto> databases;

    @NotNull
    @JsonProperty("ip_address")
    private String ipAddress;

    @NotNull
    private ImageDto image;

    @NotNull
    private Integer port;

    @NotNull
    @ApiModelProperty(name = "start time", example = "2021-03-12T15:26:21.678396092Z")
    private Instant created;

}
