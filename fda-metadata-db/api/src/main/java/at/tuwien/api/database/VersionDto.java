package at.tuwien.api.database;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VersionDto {

    @Parameter(name = "version id", example = "1")
    private Long id;

    @Parameter(name = "version creation time", example = "2020-08-04 11:12:00")
    private Instant created;

}
