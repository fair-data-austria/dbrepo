package at.tuwien.dto.database;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DatabaseDto extends DatabaseBriefDto {

    private String name;

    private String containerId;

}
