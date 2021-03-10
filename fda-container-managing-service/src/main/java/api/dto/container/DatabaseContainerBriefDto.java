package api.dto.container;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class DatabaseContainerBriefDto {

    private String id;
    private Instant created;

}
