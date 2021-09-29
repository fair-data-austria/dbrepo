package at.tuwien.service;

import at.tuwien.api.zenodo.files.FileResponseDto;
import at.tuwien.config.ZenodoConfig;
import at.tuwien.exception.ZenodoApiException;
import at.tuwien.exception.ZenodoAuthenticationException;
import at.tuwien.exception.ZenodoNotFoundException;
import at.tuwien.mapper.ZenodoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ZenodoFileService implements FileService {

    private final RestTemplate apiTemplate;
    private final RestTemplate uploadTemplate;
    private final ZenodoConfig zenodoConfig;
    private final ZenodoMapper zenodoMapper;

    @Autowired
    public ZenodoFileService(RestTemplate apiTemplate, RestTemplate uploadTemplate, ZenodoConfig zenodoConfig,
                             ZenodoMapper zenodoMapper) {
        this.apiTemplate = apiTemplate;
        this.uploadTemplate = uploadTemplate;
        this.zenodoConfig = zenodoConfig;
        this.zenodoMapper = zenodoMapper;
    }

    @Override
    public FileResponseDto createResource(Long id, String name, byte[] resource)
            throws ZenodoAuthenticationException, ZenodoApiException, ZenodoNotFoundException {
        final ResponseEntity<FileResponseDto> response = uploadTemplate.exchange("/api/deposit/depositions/{deposit_id}/files?access_token={token}",
                HttpMethod.POST, zenodoMapper.resourceToHttpEntity(name, resource), FileResponseDto.class, id,
                zenodoConfig.getApiKey());
        if (response.getStatusCode().equals(HttpStatus.UNAUTHORIZED)) {
            throw new ZenodoAuthenticationException("Token is missing or invalid.");
        }
        if (response.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
            throw new ZenodoNotFoundException("Did not find the deposit with this id");
        }
        if (response.getBody() == null) {
            throw new ZenodoApiException("Endpoint returned null body");
        }
        return response.getBody();
    }

}
