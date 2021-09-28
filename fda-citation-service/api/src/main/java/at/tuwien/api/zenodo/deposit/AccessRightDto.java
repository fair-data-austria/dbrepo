package at.tuwien.api.zenodo.deposit;

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
