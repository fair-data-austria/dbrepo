package at.tuwien.api.database.query;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;


@Data
@Getter
@Setter
@Builder
public class QueryDto {

    private Long id;

    private Timestamp executionTimestamp;

    private String query;

    private String queryNormalized;

    private String queryHash;

    private String resultHash;

    private Integer resultNumber;
}
