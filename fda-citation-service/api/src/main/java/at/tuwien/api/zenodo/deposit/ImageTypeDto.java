package at.tuwien.api.zenodo.deposit;

public enum ImageTypeDto {

    FIGURE("figure"),
    PLOT("plot"),
    DRAWING("drawing"),
    DIAGRAM("diagram"),
    PHOTO("photo"),
    OTHER("other");

    private final String type;

    ImageTypeDto(final String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type;
    }
}
