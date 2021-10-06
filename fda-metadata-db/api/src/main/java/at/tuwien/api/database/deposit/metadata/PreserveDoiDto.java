package at.tuwien.api.database.deposit.metadata;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PreserveDoiDto {

    private String doi;

    private Long recid;

}
