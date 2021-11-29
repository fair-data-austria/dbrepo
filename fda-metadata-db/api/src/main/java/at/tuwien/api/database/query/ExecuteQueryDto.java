package at.tuwien.api.database.query;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExecuteQueryDto {

    private String title;

    private String query;

}
