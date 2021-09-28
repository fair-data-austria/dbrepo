package at.tuwien.api.zenodo.deposit;

public enum UploadTypeDto {

    PUBLICATION("publication"),
    POSTER("poster"),
    PRESENTATION("presentation"),
    DATASET("dataset"),
    IMAGE("image"),
    VIDEO("video"),
    SOFTWARE("software"),
    LESSON("lesson"),
    PHYSICAL_OBJECT("physicalobject"),
    OTHER("other");

    private final String type;

    UploadTypeDto(final String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type;
    }
}
