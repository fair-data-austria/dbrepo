package at.tuwien.api.error;

import lombok.*;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@Builder
@NoArgsConstructor
public class ApiErrorDto {

    private HttpStatus status;
    private String message;
    private String code;

    public ApiErrorDto(HttpStatus status, String message, String code) {
        this.status = status;
        this.message = message;
        this.code = code;
    }

}
