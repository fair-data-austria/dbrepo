package at.tuwien.dto.table.columns;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class ColumnCreateDto {

    @NotBlank
    @Parameter(name = "column name")
    private String name;

    @NotNull
    @Parameter(name = "column type")
    private ColumnTypeDto type;

    @NotNull
    @Parameter(name = "column null")
    private Boolean nullAllowed = true;

    @Parameter(name = "column check")
    private String checkExpression;

    @Parameter(name = "column foreign key")
    private String foreignKey;

}
