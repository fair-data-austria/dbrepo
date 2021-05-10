package at.tuwien.api.database.query;

import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QueryDto {

    private Long id;

    private Timestamp executionTimestamp;

    private String query;

    private String queryNormalized;

    private String queryHash;

    private String resultHash;

    private Integer resultNumber;
}
