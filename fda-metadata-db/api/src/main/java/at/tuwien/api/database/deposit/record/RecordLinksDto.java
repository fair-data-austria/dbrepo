package at.tuwien.api.database.deposit.record;

import lombok.*;

import java.net.URI;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RecordLinksDto {

   private URI self;
}
