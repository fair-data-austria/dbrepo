package at.tuwien.api.dto.database;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CreateDatabaseResponseDto {

    private String containerId;

}