package at.tuwien.api.database.deposit;

import at.tuwien.api.database.deposit.metadata.MetadataDto;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepositChangeRequestDto {

    private MetadataDto metadata;

}
