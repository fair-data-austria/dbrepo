package at.tuwien.api.zenodo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PreserveDoiDto {

    private String doi;

    @JsonProperty("recid")
    private Long recId;

}
