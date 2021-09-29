package at.tuwien.mapper;

import org.mapstruct.Mapper;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ZenodoMapper {

    default MultiValueMap<String, HttpEntity<?>> resourceToHttpEntity(String name, MultipartFile resource) throws IOException {
        final HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        final HttpHeaders parts = new HttpHeaders();
        parts.setContentType(MediaType.TEXT_PLAIN);
        final ByteArrayResource byteArrayResource = new ByteArrayResource(resource.getBytes()) {
            @Override
            public String getFilename() {
                return name;
            }
        };
        MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
        bodyBuilder.part("file", byteArrayResource);
        bodyBuilder.part("name", name);
        return bodyBuilder.build();
    }

}
