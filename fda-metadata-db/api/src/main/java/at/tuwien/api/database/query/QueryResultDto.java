package at.tuwien.api.database.query;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class QueryResultDto {

    @NotNull(message = "result set is required")
    @ApiModelProperty(notes = "query result")
    private List<Map<String, Object>> result;

    @NotNull(message = "query id is required")
    @ApiModelProperty(notes = "query id")
    private Long id;

    @ApiModelProperty(notes = "result number")
    private Long resultNumber;

}
