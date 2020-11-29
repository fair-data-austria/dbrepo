package at.tuwien.querystore.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
public class Query  {
	private Timestamp execTimestamp;
	private String query;
	private String reWrittenQuery;
	private String queryHash;
	private String resultsetHash;

}
