package at.tuwien.dto.table.columns;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class ColumnDto {

    @NotBlank
    @Parameter(name = "column type")
    private ColumnTypeDto type;

    @Parameter(name = "categories", description = "only categorical, derive totalCategories from array")
    private String[] categories;

    @Parameter(name = "categories", description = "only nominal and numerical")
    private Double maxLength;

    @Parameter(name = "categories", description = "only numerical")
    private SiUnitDto siUnit;

    @Parameter(name = "categories", description = "only numerical")
    private Double min;

    @Parameter(name = "categories", description = "only numerical")
    private Double max;

    @Parameter(name = "categories", description = "only numerical")
    private Double mean;

    @Parameter(name = "categories", description = "only numerical")
    private Double median;

    @Parameter(name = "categories", description = "only numerical")
    private Double standardDeviation;

    @Parameter(name = "categories", description = "only numerical")
    private Object histogram;

}
