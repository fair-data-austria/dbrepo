package at.tuwien.api.zenodo.deposit;

public enum PublicationTypeDto {

    ANNOTATION_COLLECTION("annotationcollection"),
    BOOK("book"),
    SECTION("section"),
    CONFERENCE_PAPER("conferencepaper"),
    DATA_MANAGEMENT_PLAN("datamanagementplan"),
    ARTICLE("article"),
    PATENT("patent"),
    PREPRINT("preprint"),
    PROJECT_DELIVERABLE("deliverable"),
    PROJECT_MILESTONE("milestone"),
    PROPOSAL("proposal"),
    REPORT("report"),
    SOFTWARE_DOCUMENTATION("softwaredocumentation"),
    TAXONOMIC_TREATMENT("taxonomictreatment"),
    TECHNICAL_NOTE("technicalnote"),
    THESIS("thesis"),
    WORKING_PAPER("workingpaper"),
    OTHER("other");

    private final String type;

    PublicationTypeDto(final String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type;
    }
}
