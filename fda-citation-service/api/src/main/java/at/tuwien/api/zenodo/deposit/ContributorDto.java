package at.tuwien.api.zenodo.deposit;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContributorDto {

    /**
     * e.g. Rauber, Andreas
     */
    private String name;

    private String affiliation;

    private String orcid;

    private String gnd;

}
