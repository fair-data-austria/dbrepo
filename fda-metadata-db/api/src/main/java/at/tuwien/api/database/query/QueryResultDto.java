package at.tuwien.api.database.query;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class QueryResultDto {

    @NotNull
    @ApiModelProperty(name = "query result")
    private List<Map<String, Object>> result;

}
