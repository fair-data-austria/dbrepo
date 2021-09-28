package at.tuwien.api.zenodo.deposit;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ImageTypeDto {

    @JsonProperty("figure")
    FIGURE,

    @JsonProperty("plot")
    PLOT,

    @JsonProperty("drawing")
    DRAWING,

    @JsonProperty("diagram")
    DIAGRAM,

    @JsonProperty("photo")
    PHOTO,

    @JsonProperty("other")
    OTHER;
}
