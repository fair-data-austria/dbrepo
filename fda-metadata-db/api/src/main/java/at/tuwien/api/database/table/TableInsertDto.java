package at.tuwien.api.database.table;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TableInsertDto {

    @NotBlank
    @ApiModelProperty(name = "null element", example = "NA")
    private String nullElement;

}
