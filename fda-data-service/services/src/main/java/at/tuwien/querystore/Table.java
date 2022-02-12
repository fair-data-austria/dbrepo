package at.tuwien.querystore;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.List;

@Data
@Entity
@javax.persistence.Table(name = "qs_tables")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EntityListeners(AuditingEntityListener.class)
public class Table implements Serializable {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(generator = "table-sequence")
    @GenericGenerator(
            name = "table-sequence",
            strategy = "enhanced-sequence",
            parameters = @org.hibernate.annotations.Parameter(name = "sequence_name", value = "qs_tables_seq")
    )
    private Long id;

    @javax.persistence.Column(nullable = false)
    private Long dbid;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    private List<Column> columns;

    @javax.persistence.Column(nullable = false, updatable = false)
    @CreatedDate
    private Instant created;

    @javax.persistence.Column(name = "last_modified")
    @LastModifiedDate
    private Instant lastModified;

}
