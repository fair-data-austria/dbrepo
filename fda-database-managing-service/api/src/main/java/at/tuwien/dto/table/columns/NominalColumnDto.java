package at.tuwien.dto.table.columns;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NominalColumnDto extends AbstractColumnDto {

    private Long maxLength;

}
