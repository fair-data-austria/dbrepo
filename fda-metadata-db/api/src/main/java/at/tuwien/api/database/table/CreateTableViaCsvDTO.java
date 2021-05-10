package at.tuwien.api.database.table;

import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateTableViaCsvDTO {

    private String containerId;

    private String pathToFile;

    private char delimiter;


}
