package at.tuwien.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class ExecuteStatementDTO {
    @JsonProperty("ContainerID")
    private String containerID;
    @JsonProperty("Statement")
    private String statement;

}
