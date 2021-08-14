package at.tuwien.api.amqp;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DataDto {

    @NotNull
    @ApiModelProperty(name = "database id", example = "1")
    private Integer databaseId;

    @NotNull
    @ApiModelProperty(name = "table id", example = "1")
    private Integer tableId;

    @NotNull
    @ApiModelProperty(name = "data", example = "[\"k\":\"name\",\"v\":\"Max\"]")
    private TupleDto[] data;

}
