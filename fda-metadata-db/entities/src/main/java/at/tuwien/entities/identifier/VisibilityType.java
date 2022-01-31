package at.tuwien.entities.identifier;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public enum VisibilityType {
    EVERYONE,
    TRUSTED,
    SELF;
}