package at.tuwien.api.database.deposit.files;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum FileTypeDto {

    @JsonProperty("csv")
    CSV,

    @JsonProperty("other")
    OTHER;
}
