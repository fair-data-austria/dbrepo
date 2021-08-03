package at.tuwien.entities.database.query;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.Instant;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EntityListeners(AuditingEntityListener.class)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "mdb_queries")
public class Query  {

	@Id
	@EqualsAndHashCode.Include
	@ToString.Include
	@GeneratedValue(generator = "query-sequence")
	@GenericGenerator(
			name = "query-sequence",
			strategy = "enhanced-sequence",
			parameters = @org.hibernate.annotations.Parameter(name = "sequence_name", value = "mdb_queries_seq")
	)
	private Long id;

	@ToString.Include
	@Column(nullable = false)
	private Timestamp executionTimestamp;

	@ToString.Include
	@Column(nullable = false)
	private String query;

	@ToString.Include
	@Column
	private String queryNormalized;

	@ToString.Include
	@Column(nullable = false)
	private String queryHash;

	@ToString.Include
	@Column
	private String resultHash;

	@ToString.Include
	@Column
	private Integer resultNumber;

	@Column(nullable = false, updatable = false)
	@CreatedDate
	private Instant created;

	@Column
	@LastModifiedDate
	private Instant lastModified;

}
