package at.tuwien.dto.table.columns;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NumericColumnDto extends AbstractColumnDto {

    private Long maxLength;

    private SiUnitDto siUnit;

    private Double min;

    private Double max;

    private Double mean;

    private Double median;

    private Double standardDeviation;

    private Object histogram;

}
