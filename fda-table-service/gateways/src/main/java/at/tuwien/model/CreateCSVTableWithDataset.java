package at.tuwien.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class CreateCSVTableWithDataset {
    @JsonProperty("ContainerID")
    private String containerID;
    @JsonProperty("PathToCSVFile")
    private String pathToCSVFile;
    @JsonProperty("TableName")
    private String tableName;
    @JsonProperty("ColumnNames")
    private String columnNames;

}
