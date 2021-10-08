package at.tuwien.api.database.deposit.files;

import at.tuwien.api.database.query.QueryDto;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileDto {

    private Long id;

    private Long fqid;

    private Long fdbid;

    private String refId;

    private QueryDto query;

    private Instant created;

    private Instant lastModified;
}
