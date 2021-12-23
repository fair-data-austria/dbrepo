package at.tuwien.api.database.deposit.metadata;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IdentifierDto {

    private String identifier;

    private String relation;

    private String scheme;

}
