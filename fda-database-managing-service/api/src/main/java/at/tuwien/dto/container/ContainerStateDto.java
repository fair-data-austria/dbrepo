package at.tuwien.dto.container;

import lombok.Getter;

@Getter
public enum ContainerStateDto {
    CREATED, RESTARTING, RUNNING, PAUSED, EXITED, DEAD
}
