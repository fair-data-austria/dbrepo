package at.tuwien.api.database.table.columns;

import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RowDto {

    @NotNull
    private ColumnDto[] columns;

    @NotNull
    private String[] data;

}
