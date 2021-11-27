package at.tuwien.api.database.table;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TableInsertDto {

    @ApiModelProperty(name = "null element", example = "NA", required = true)
    private String nullElement;

    @ApiModelProperty(name = "delimiting character", example = ",", required = true)
    private Character delimiter = ',';

    @ApiModelProperty(name = "skip the first line", example = "false", required = true)
    private Boolean skipHeader = false;

    @ApiModelProperty(name = "element for true", example = "1")
    private String trueElement = "1";

    @ApiModelProperty(name = "element for false", example = "0")
    private String falseElement = "0";

    @NotBlank
    @ApiModelProperty(name = "csv file location", required = true)
    private String csvLocation;

}
