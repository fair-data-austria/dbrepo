package at.tuwien.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class CopyCSVIntoTableDTO {
    @JsonProperty("ContainerID")
    private String containerID;
    @JsonProperty("PathToCSVFile")
    private String pathToCSVFile;
    @JsonProperty("TableName")
    private String tableName;
    @JsonProperty("ColumnNames")
    private String columnNames;
    @JsonProperty("Delimiter")
    private char delimiter;

}
