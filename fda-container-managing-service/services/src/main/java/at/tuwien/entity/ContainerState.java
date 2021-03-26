package at.tuwien.entity;

import lombok.Getter;

@Getter
public enum ContainerState {
    CREATED, RESTARTING, RUNNING, PAUSED, EXITED, DEAD
}
