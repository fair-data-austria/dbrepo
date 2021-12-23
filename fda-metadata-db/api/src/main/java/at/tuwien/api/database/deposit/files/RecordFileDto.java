package at.tuwien.api.database.deposit.files;

import at.tuwien.api.database.deposit.metadata.ResourceTypeDto;
import at.tuwien.api.database.deposit.record.RecordLinksDto;
import at.tuwien.api.database.query.QueryDto;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RecordFileDto {

    private String bucket;

    private String checksum;

    private String key;

    private RecordLinksDto links;

    private ResourceTypeDto type;

}
