package at.tuwien.api.database.deposit.metadata;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreatorDto {

    private String name;

    private String affiliation;

    private String orcid;

    private String gnd;

}
