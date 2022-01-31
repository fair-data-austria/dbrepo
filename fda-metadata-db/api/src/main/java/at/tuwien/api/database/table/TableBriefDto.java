package at.tuwien.api.database.table;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class TableBriefDto {

    @NotNull
    @ApiModelProperty(name = "table id", example = "1")
    private Long id;

    @NotBlank
    @ApiModelProperty(name = "table name", example = "Weather Australia")
    private String name;

    @NotBlank
    @JsonProperty("internal_name")
    @ApiModelProperty(name = "table internal name", example = "weather_australia")
    private String internalName;

}
