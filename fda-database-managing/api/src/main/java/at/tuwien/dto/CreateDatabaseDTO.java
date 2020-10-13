package at.tuwien.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateDatabaseDTO {

    private String containerName;
    private String dbName;

}
