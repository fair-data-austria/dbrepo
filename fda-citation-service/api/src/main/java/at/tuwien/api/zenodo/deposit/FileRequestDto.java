package at.tuwien.api.zenodo.deposit;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileRequestDto {

    private String name;

}
