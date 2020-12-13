package at.tuwien.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class CSVColumnsResult {

    @JsonProperty("pathToFile")
    private String pathToFile;

    @JsonProperty("columns")
    private Map<String,String> columns;
}
