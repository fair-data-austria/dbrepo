package at.tuwien.api.identifier;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public enum VisibilityTypeDto {
    EVERYONE,
    TRUSTED,
    SELF;
}