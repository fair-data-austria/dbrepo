package at.tuwien.api.zenodo.deposit;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum UploadTypeDto {

    @JsonProperty("publication")
    PUBLICATION,

    @JsonProperty("poster")
    POSTER,

    @JsonProperty("presentation")
    PRESENTATION,

    @JsonProperty("dataset")
    DATASET,

    @JsonProperty("image")
    IMAGE,

    @JsonProperty("video")
    VIDEO,

    @JsonProperty("software")
    SOFTWARE,

    @JsonProperty("lesson")
    LESSON,

    @JsonProperty("physicalobject")
    PHYSICAL_OBJECT,

    @JsonProperty("other")
    OTHER;
}
