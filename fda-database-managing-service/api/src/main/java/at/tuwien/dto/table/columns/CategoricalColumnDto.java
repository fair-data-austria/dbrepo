package at.tuwien.dto.table.columns;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoricalColumnDto extends AbstractColumnDto {

    private Long totalCategories;

    private String[] categories;

}
