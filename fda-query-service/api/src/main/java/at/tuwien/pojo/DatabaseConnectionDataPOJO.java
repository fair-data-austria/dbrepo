package at.tuwien.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class DatabaseConnectionDataPOJO {
    @JsonProperty("IpAddress")
    private String ipAddress;
    @JsonProperty("DatabaseName")
    private String dbName;
}
