package at.tuwien.entities.user;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.Instant;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EntityListeners(AuditingEntityListener.class)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "mdb_users")
public class User {

    @Id
    @EqualsAndHashCode.Include
    @Column(name = "userid", columnDefinition = "numeric(19, 2)")
    @GeneratedValue(generator = "user-sequence")
    @GenericGenerator(
            name = "user-sequence",
            strategy = "enhanced-sequence",
            parameters = @org.hibernate.annotations.Parameter(name = "sequence_name", value = "mdb_user_seq")
    )
    private Long id;

    @EqualsAndHashCode.Include
    @Column(name = "oid", unique = true)
    private Long oId;

    @Column(name = "first_name", nullable = false)
    private String firstname;

    @Column(name = "last_name", nullable = false)
    private String lastname;

    @Column(name = "preceding_titles")
    private String titlesBefore;

    @Column(name = "postpositioned_title")
    private String titlesAfter;

    @Column(name = "main_email")
    private String email;

    @Column(nullable = false, updatable = false)
    @CreatedDate
    private Instant created;

    @Column
    @LastModifiedDate
    private Instant lastModified;

}
