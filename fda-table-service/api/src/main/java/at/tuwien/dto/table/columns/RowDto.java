package at.tuwien.dto.table.columns;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
public class RowDto {

    @NotNull
    private ColumnDto[] columns;

    @NotNull
    private String[] data;

}
