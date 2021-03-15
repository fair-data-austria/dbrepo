package at.tuwien.api.dto.database;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class DatabaseContainerCreateRequestDto {

    private String containerName;

    private String databaseName;

    private String image;

}
