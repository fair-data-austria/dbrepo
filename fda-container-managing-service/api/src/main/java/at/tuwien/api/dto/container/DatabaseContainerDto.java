package at.tuwien.api.dto.container;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DatabaseContainerDto extends ContainerDto {

    private String databaseName;

}
