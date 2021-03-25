package at.tuwien.dto.table.columns;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class ColumnCreateDto {

    @NotBlank
    @Parameter(name = "column name")
    private String name;

    @NotBlank
    @Parameter(name = "column type")
    private ColumnTypeDto type;

    @NotBlank
    @Parameter(name = "column null")
    private Boolean nullAllowed = true;

    @Parameter(name = "column check")
    private String checkExpression;

    @Parameter(name = "column foreign key")
    private String foreignKey;


}
