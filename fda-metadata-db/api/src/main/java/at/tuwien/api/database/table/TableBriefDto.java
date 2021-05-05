package at.tuwien.api.database.table;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
public class TableBriefDto {

    @NotNull
    @ApiModelProperty(name = "table id", example = "1")
    private Long id;

    @NotBlank
    @ApiModelProperty(name = "table name", example = "Fundamentals")
    private String name;

    @NotBlank
    @ApiModelProperty(name = "table internal name", example = "fundamentals")
    private String internalName;

}
