package at.tuwien.api.database;

import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DatabaseChangeDto {

    @NotNull
    private Long databaseId;

}
