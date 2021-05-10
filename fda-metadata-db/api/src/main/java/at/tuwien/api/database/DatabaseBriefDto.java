package at.tuwien.api.database;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DatabaseBriefDto {

    @NotNull
    @Min(value = 1)
    @Parameter(name = "at.tuwien.database id", example = "1")
    private Long id;

    @NotBlank
    @Parameter(name = "at.tuwien.database name", example = "Exchange Traded Funds")
    private String name;

    @NotBlank
    @Parameter(name = "at.tuwien.database internal name", example = "exchange_traded_funds")
    private String internalName;

}
