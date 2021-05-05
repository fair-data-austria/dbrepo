package at.tuwien.api.database.table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class CreateTableViaCsvDTO {

    private String containerId;

    private String pathToFile;

    private char delimiter;


}
