package at.tuwien.querystore;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;
import java.time.Instant;

@Data
@Entity
@javax.persistence.Table(name = "qs_column")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EntityListeners(AuditingEntityListener.class)
public class Column implements Serializable {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(generator = "column-sequence")
    @GenericGenerator(
            name = "column-sequence",
            strategy = "enhanced-sequence",
            parameters = @org.hibernate.annotations.Parameter(name = "sequence_name", value = "qs_column_seq")
    )
    private Long id;

    @javax.persistence.Column(nullable = false)
    private Long tid;

    @javax.persistence.Column(nullable = false)
    private Long dbid;

    @javax.persistence.Column(nullable = false, updatable = false)
    @CreatedDate
    private Instant created;

    @javax.persistence.Column(name = "last_modified")
    @LastModifiedDate
    private Instant lastModified;

}
