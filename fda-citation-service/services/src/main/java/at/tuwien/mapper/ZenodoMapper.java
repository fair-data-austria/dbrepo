package at.tuwien.mapper;

import at.tuwien.api.zenodo.deposit.FileBinaryRequestDto;
import at.tuwien.api.zenodo.deposit.FileRequestDto;
import org.mapstruct.Mapper;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring")
public interface ZenodoMapper {

    default HttpEntity<MultiValueMap<String, Object>> resourceToHttpEntity(String name, byte[] resource) {
        final HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        final HttpHeaders parts = new HttpHeaders();
        parts.setContentType(MediaType.TEXT_PLAIN);
        final ByteArrayResource byteArrayResource = new ByteArrayResource(resource) {
            @Override
            public String getFilename() {
                return name;
            }
        };
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new HttpEntity<>(byteArrayResource, parts));
        body.add("data", new HttpEntity<>(name, parts));
        return new HttpEntity<>(body, headers);
    }

}
