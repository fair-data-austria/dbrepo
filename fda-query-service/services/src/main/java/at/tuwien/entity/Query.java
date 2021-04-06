package at.tuwien.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.sql.Timestamp;

@Data
@Getter
@Setter
@NoArgsConstructor
public class Query  {

	@Id
	@EqualsAndHashCode.Include
	@ToString.Include
	private Long id;

	@Column
	private Timestamp execTimestamp;
	@Column
	private String query;
	@Column
	private String reWrittenQuery;
	@Column
	private String queryHash;
	@Column
	private String resultsetHash;

}
