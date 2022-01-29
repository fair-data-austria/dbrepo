package at.tuwien.api.database.table;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TableCsvDto {

    @NotBlank
    @ApiModelProperty(name = "data")
    private List<List<Object>> data;

    @NotBlank
    @ApiModelProperty(name = "header")
    private List<String> header;

}
