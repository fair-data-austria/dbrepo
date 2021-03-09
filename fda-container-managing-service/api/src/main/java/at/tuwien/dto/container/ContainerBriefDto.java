package at.tuwien.dto.container;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class ContainerBriefDto {

    private String id;
    private Instant created;

}
