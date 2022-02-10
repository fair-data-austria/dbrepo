package at.tuwien.api.database.table;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TableCsvDto {

    @NotNull(message = "data is required")
    @ApiModelProperty(name = "data")
    private Map<String, Object> data;

}
