package at.tuwien.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class CreateTableViaCsvDTO {

    @JsonProperty("ContainerID")
    private String containerID;
    @JsonProperty("PathToFile")
    private String pathToFile;
    @JsonProperty("Delimiter")
    private char delimiter;


}
