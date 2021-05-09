package at.tuwien.api.database.query;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class ExecuteInternalQueryDto {

    @JsonProperty("ContainerID")
    private String containerId;

    @JsonProperty("Query")
    private String query;

}
