package at.tuwien.entity;

import lombok.Builder;
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
@Builder
public class Query  {


	@EqualsAndHashCode.Include
	@ToString.Include
	private Long id;

	private Timestamp execution_timestamp;

	private String query;

	private String query_normalized;

	private String query_hash;

	private String result_hash;

	private String result_number;

}
