package at.tuwien.dto.table.columns;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RowDto {

    private ColumnDto[] columns;

    private String[] data;

}
