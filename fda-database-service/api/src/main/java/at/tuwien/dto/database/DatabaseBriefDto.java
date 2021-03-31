package at.tuwien.dto.database;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@ToString
public class DatabaseBriefDto {

    @NotNull
    @Min(value = 1)
    @Parameter(name = "database id", example = "1")
    private Long id;

    @NotBlank
    @Parameter(name = "database name", example = "Exchange Traded Funds")
    private String name;

    @NotBlank
    @Parameter(name = "database internal name", example = "exchange_traded_funds")
    private String internalName;

}