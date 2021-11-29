package at.tuwien.mapper;

import at.tuwien.api.database.deposit.DepositDto;
import at.tuwien.api.database.deposit.DepositTzDto;
import at.tuwien.api.database.query.QueryDto;
import at.tuwien.entities.database.query.Query;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
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

    @Mappings({
            @Mapping(source = "metadata.prereserveDoi.doi", target = "doi"),
            @Mapping(source = "id", target = "depositId"),
            @Mapping(ignore = true, target = "id")
    })
    Query depositTzDtoToQuery(DepositTzDto data);

    default Instant localDateTimeToInstant(LocalDateTime data) {
        return data.toInstant(ZoneOffset.UTC);
    }

    @Mappings({
            @Mapping(source = "created", target = "executionTimestamp"),
            @Mapping(source = "metadata.prereserveDoi.doi", target = "doi"),
            @Mapping(source = "recordId", target = "depositId"),
    })
    QueryDto depositChangeResponseDtoToQueryDto(DepositDto data);

}
