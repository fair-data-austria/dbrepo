package at.tuwien.api.zenodo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MetadataDto {

    @JsonProperty("prereserve_doi")
    private PreserveDoiDto prereserveDoi;

}
