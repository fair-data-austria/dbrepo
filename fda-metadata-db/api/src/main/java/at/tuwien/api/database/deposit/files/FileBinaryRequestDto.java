package at.tuwien.api.database.deposit.files;

import lombok.*;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class FileBinaryRequestDto {

    private HttpEntity<ByteArrayResource> file;

}
