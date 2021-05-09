package at.tuwien.entities.database.query;

import lombok.*;

import java.sql.Timestamp;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Query  {

	@EqualsAndHashCode.Include
	@ToString.Include
	private Long id;

	private Timestamp executionTimestamp;

	private String query;

	private String queryNormalized;

	private String queryHash;

	private String resultHash;

	private Integer resultNumber;

}
