package at.tuwien.api.container;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public enum ContainerStateDto {
    CREATED, RESTARTING, RUNNING, PAUSED, EXITED, DEAD
}
