package at.tuwien.api.zenodo.deposit;

public enum AccessTypeDto {

    OPEN("open"),
    EMBARGOED("embargoed"),
    RESTRICTED("restricted"),
    CLOSED("closed");

    private final String type;

    AccessTypeDto(final String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type;
    }
}
