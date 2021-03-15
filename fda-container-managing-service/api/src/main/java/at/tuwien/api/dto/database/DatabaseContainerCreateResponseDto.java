package at.tuwien.api.dto.database;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class DatabaseContainerCreateResponseDto {

    private String containerId;

}
