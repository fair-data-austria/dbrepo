package at.tuwien.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class CreateDatabaseConnectionDataDTO {

    @JsonProperty("IpAddress")
    private String ipAddress;
    @JsonProperty("DatabaseName")
    private String dbName;
}
