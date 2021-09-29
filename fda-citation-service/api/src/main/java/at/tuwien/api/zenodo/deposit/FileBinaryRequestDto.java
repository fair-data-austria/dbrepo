package at.tuwien.api.zenodo.deposit;

import lombok.*;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileBinaryRequestDto {

    private HttpEntity<ByteArrayResource> file;

}
