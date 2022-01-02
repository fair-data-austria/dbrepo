package at.tuwien.api.database.query;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Deprecated
public class ExecuteInternalQueryDto {

    @JsonProperty("ContainerID")
    private String containerId;

    @JsonProperty("Query")
    private String query;

}
