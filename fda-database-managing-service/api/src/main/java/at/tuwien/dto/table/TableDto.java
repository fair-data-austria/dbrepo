package at.tuwien.dto.table;

import at.tuwien.dto.table.columns.AbstractColumnDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TableDto extends TableBriefDto {

    private String name;

    private AbstractColumnDto[] columns;

    private String description;

}
