package at.tuwien.api.zenodo.deposit;

/**
 * @apiNote https://sandbox.zenodo.org/api/licenses/
 */
public enum LicenseTypeDto {

    BSD("bsd-license"),
    CC_NC("cc-nc"),
    CC_BY("cc-by");

    private final String type;

    LicenseTypeDto(final String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type;
    }
}
