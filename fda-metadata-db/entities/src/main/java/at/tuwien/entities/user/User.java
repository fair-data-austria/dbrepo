package at.tuwien.entities.user;

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
@Table(name = "mdb_users")
public class User {

	@Id
	@EqualsAndHashCode.Include
	@ToString.Include
	@Column(name = "userid")
	@GeneratedValue(generator = "sequence-per-entity")
	@GenericGenerator(
			name = "sequence-per-entity",
			strategy = "enhanced-sequence",
			parameters = @org.hibernate.annotations.Parameter(name = "prefer_sequence_per_entity", value = "true")
	)
	private Long id;

	@ToString.Include
	@EqualsAndHashCode.Include
	@Column(name = "tiss_id", unique = true)
	private Long tissId;

	@ToString.Include
	@EqualsAndHashCode.Include
	@Column(name = "oid", nullable = false)
	private Long organizationid;

	@ToString.Include
	@Column(name = "first_name", nullable = false)
	private String firstname;

	@ToString.Include
	@Column(name = "last_name", nullable = false)
	private String lastname;

	@ToString.Include
	@Column(name = "preceding_titles")
	private String titlesBefore;

	@ToString.Include
	@Column(name = "postpositioned_title")
	private String titlesAfter;

	@ToString.Include
	@Column(name = "main_email")
	private String email;

	@ToString.Include
	@Column(nullable = false)
	private Gender gender;

	@Column(nullable = false, updatable = false)
	@CreatedDate
	private Instant created;

	@Column
	@LastModifiedDate
	private Instant lastModified;

}
