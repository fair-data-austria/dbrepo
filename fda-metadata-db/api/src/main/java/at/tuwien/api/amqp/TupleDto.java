package at.tuwien.api.amqp;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TupleDto {

    @NotNull
    @ApiModelProperty(name = "key", example = "name")
    private String k;

    @NotNull
    @ApiModelProperty(name = "value", example = "Max Mustermann")
    private String v;

}
