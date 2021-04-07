package at.tuwien.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;


@Data
@Getter
@Setter
@Builder
public class QueryDto {
    private Long id;

    private Timestamp execution_timestamp;

    private String query;

    private String query_normalized;

    private String query_hash;

    private String result_hash;

    private String result_number;
}
