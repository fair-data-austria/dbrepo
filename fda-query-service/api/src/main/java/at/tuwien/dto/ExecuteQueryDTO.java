package at.tuwien.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class ExecuteQueryDTO {

    @JsonProperty("ContainerID")
    private String containerID;
    @JsonProperty("Query")
    private String query;

}
