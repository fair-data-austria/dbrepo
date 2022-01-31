package at.tuwien.api.container.image;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Instant;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ImageDateDto {

    @NotNull
    @ApiModelProperty(required = true, example = "1")
    private Long id;

    @NotBlank
    @ApiModelProperty(required = true, example = "30.01.2022")
    private String example;

    @NotBlank
    @JsonProperty("database_format")
    @ApiModelProperty(required = true, example = "%d.%c.%Y")
    private String databaseFormat;

    @NotBlank
    @JsonProperty("unix_format")
    @ApiModelProperty(required = true, example = "dd.mm.YYYY")
    private String unixFormat;

    @JsonProperty("created_at")
    private Instant createdAt;

}
