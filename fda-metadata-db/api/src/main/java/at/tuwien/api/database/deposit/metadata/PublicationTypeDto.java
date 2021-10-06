package at.tuwien.api.database.deposit.metadata;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum PublicationTypeDto {

    @JsonProperty("annotationcollection")
    ANNOTATION_COLLECTION,

    @JsonProperty("book")
    BOOK,

    @JsonProperty("section")
    SECTION,

    @JsonProperty("conferencepaper")
    CONFERENCE_PAPER,

    @JsonProperty("datamanagementplan")
    DATA_MANAGEMENT_PLAN,

    @JsonProperty("article")
    ARTICLE,

    @JsonProperty("patent")
    PATENT,

    @JsonProperty("preprint")
    PREPRINT,

    @JsonProperty("deliverable")
    PROJECT_DELIVERABLE,

    @JsonProperty("milestone")
    PROJECT_MILESTONE,

    @JsonProperty("proposal")
    PROPOSAL,

    @JsonProperty("report")
    REPORT,

    @JsonProperty("softwaredocumentation")
    SOFTWARE_DOCUMENTATION,

    @JsonProperty("taxonomictreatment")
    TAXONOMIC_TREATMENT,

    @JsonProperty("technicalnote")
    TECHNICAL_NOTE,

    @JsonProperty("thesis")
    THESIS,

    @JsonProperty("workingpaper")
    WORKING_PAPER,

    @JsonProperty("other")
    OTHER;
}
