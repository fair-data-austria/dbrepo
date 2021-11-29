package at.tuwien.api.database.deposit.metadata;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum AccessRightDto {

    @JsonProperty("open")
    OPEN,

    @JsonProperty("embargoed")
    EMBARGOED,

    @JsonProperty("restricted")
    RESTRICTED,

    @JsonProperty("closed")
    CLOSED;
}
