package at.tuwien.api.database.query;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ImportDto {

    @NotBlank(message = "location is required")
    @ApiModelProperty(name = "csv location")
    private String location;
}
