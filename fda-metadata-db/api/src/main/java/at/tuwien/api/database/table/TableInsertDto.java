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

    @ApiModelProperty(name = "null element", example = "NA")
    private String nullElement;

    @ApiModelProperty(name = "delimiting character", example = ";")
    private Character delimiter = ',';

    @ApiModelProperty(name = "skip the first line", example = "false")
    private Boolean skipHeader = false;

    @NotBlank
    @ApiModelProperty(name = "csv file location")
    private String csvLocation;

}
