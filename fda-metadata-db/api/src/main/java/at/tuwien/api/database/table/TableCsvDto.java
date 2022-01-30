package at.tuwien.api.database.table;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TableCsvDto {

    @NotNull
    @ApiModelProperty(name = "data")
    private List<List<Object>> data;

    @NotNull
    @ApiModelProperty(name = "header")
    private List<String> header;

}
