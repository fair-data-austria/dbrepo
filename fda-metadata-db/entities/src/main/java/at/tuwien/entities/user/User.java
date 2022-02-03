package at.tuwien.entities.user;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Set;

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

    @Column(unique = true, nullable = false)
    private String username;

    @Column(name = "first_name")
    private String firstname;

    @Column(name = "last_name")
    private String lastname;

    @Column(name = "preceding_titles")
    private String titlesBefore;

    @Column(name = "postpositioned_title")
    private String titlesAfter;

    @Column(name = "main_email", unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @ElementCollection(targetClass = RoleType.class)
    @JoinTable(name = "mdb_user_roles", joinColumns = @JoinColumn(name = "uid"))
    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private List<RoleType> roles;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Instant created;

    @LastModifiedDate
    @Column(name = "last_modified")
    private Instant lastModified;

}
