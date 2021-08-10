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
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EntityListeners(AuditingEntityListener.class)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Query  {

	@EqualsAndHashCode.Include
	@ToString.Include
	private Long id;

	@ToString.Include
	private String doi;

	@ToString.Include
	private Timestamp executionTimestamp;

	@ToString.Include
	private String query;

	@ToString.Include
	private String queryNormalized;

	@ToString.Include
	private String queryHash;

	@ToString.Include
	private String resultHash;

	@ToString.Include
	private Integer resultNumber;


}
