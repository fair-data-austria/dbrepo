package at.tuwien.api.database.query;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExecuteQueryDto {

    @NotBlank
    @ApiModelProperty(name = "query title", example = "Weather in Alberta", required = true)
    private String title;

    @NotBlank
    @ApiModelProperty(name = "query description", example = "Data from 2018-2021 considering only values > 0",
            required = true)
    private String description;

    @NotBlank
    @ApiModelProperty(name = "query raw", example = "select * from table", required = true)
    private String query;

}
