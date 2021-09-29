package at.tuwien.mapper;

import org.apache.commons.io.FileUtils;
import org.mapstruct.Mapper;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ZenodoMapper {

    default MultiValueMap<String, HttpEntity<?>> resourceToHttpEntity(String name, File resource) throws IOException {
        final HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        final HttpHeaders parts = new HttpHeaders();
        parts.setContentType(MediaType.TEXT_PLAIN);
        final ByteArrayResource byteArrayResource = new ByteArrayResource(FileUtils.readFileToByteArray(resource)) {
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
