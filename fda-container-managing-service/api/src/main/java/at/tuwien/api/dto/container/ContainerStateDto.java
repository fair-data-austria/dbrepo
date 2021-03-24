package at.tuwien.api.dto.container;

import lombok.Getter;

@Getter
public enum ContainerStateDto {
    CREATED, RESTARTING, RUNNING, PAUSED, EXITED, DEAD
}
