package at.tuwien.entities;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.time.Instant;


@Entity
@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
public class DatabaseContainer extends Auditable{

    @Column(nullable = false)
    private String containerId;

    @Column(nullable = false)
    private Instant containerCreated;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String databaseName;

    @Column
    private String status;

    @Column
    private String ipAddress;

}
