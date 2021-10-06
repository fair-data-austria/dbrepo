package at.tuwien.api.database.deposit.metadata;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @apiNote https://sandbox.zenodo.org/api/licenses/
 */
public enum LicenseTypeDto {

    @JsonProperty("bsd-license")
    BSD,

    @JsonProperty("cc-nc")
    CC_NC,

    @JsonProperty("cc-by")
    CC_BY,

    @JsonProperty("CC0-1.0")
    CC0_1_0;
}
