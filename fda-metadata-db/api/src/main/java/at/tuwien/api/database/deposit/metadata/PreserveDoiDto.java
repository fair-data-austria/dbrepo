package at.tuwien.api.database.deposit.metadata;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PreserveDoiDto {

    private String doi;

    private Long recid;

}
