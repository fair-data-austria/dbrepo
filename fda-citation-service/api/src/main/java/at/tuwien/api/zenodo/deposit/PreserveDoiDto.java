package at.tuwien.api.zenodo.deposit;

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
