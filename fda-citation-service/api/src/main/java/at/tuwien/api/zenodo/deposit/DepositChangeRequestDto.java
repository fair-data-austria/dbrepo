package at.tuwien.api.zenodo.deposit;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepositChangeRequestDto {

    private MetadataDto metadata;

}
