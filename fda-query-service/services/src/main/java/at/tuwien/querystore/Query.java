package at.tuwien.querystore;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "query_store")
public class Query implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2135318549456223860L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int pid;

	@Column(name = "exec_timestamp")
	private Timestamp execTimestamp;

	@Column(name = "query")
	private String query;

	@Column(name = "resourceName")
	private String resourceName;

	@Column(name = "query_hash")
	private String queryHash;

	@Column(name = "resultset_hash")
	private String resultsetHash;

}
