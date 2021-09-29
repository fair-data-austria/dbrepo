package at.tuwien.service;

import at.tuwien.api.zenodo.deposit.*;
import at.tuwien.config.ZenodoConfig;
import at.tuwien.exception.ZenodoApiException;
import at.tuwien.exception.ZenodoAuthenticationException;
import at.tuwien.exception.ZenodoNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
public class ZenodoMetadataService implements MetadataService {

    private final RestTemplate apiTemplate;
    private final ZenodoConfig zenodoConfig;

    @Autowired
    public ZenodoMetadataService(RestTemplate apiTemplate, ZenodoConfig zenodoConfig) {
        this.apiTemplate = apiTemplate;
        this.zenodoConfig = zenodoConfig;
    }

    @Override
    public List<DepositResponseDto> listCitations() throws ZenodoAuthenticationException, ZenodoApiException {
        final ResponseEntity<DepositResponseDto[]> response = apiTemplate.exchange("/api/deposit/depositions?access_token={token}",
                HttpMethod.GET, null, DepositResponseDto[].class, zenodoConfig.getApiKey());
        if (response.getStatusCode().equals(HttpStatus.UNAUTHORIZED)) {
            throw new ZenodoAuthenticationException("Token is missing or invalid.");
        }
        if (response.getBody() == null) {
            throw new ZenodoApiException("Endpoint returned null body");
        }
        return Arrays.asList(response.getBody());
    }

    @Override
    public DepositChangeResponseDto storeCitation() throws ZenodoAuthenticationException, ZenodoApiException {
        final ResponseEntity<DepositChangeResponseDto> response = apiTemplate.exchange("/api/deposit/depositions?access_token={token}",
                HttpMethod.POST, new HttpEntity<>("{}"), DepositChangeResponseDto.class, zenodoConfig.getApiKey());
        if (response.getStatusCode().equals(HttpStatus.UNAUTHORIZED)) {
            throw new ZenodoAuthenticationException("Token is missing or invalid.");
        }
        if (response.getStatusCode().equals(HttpStatus.BAD_REQUEST)) { // 400
            throw new ZenodoApiException("Failed to store citation.");
        }
        if (response.getBody() == null) {
            throw new ZenodoApiException("Endpoint returned null body");
        }
        return response.getBody();
    }

    @Override
    public DepositChangeResponseDto updateCitation(Long id, DepositChangeRequestDto data) throws ZenodoAuthenticationException,
            ZenodoApiException, ZenodoNotFoundException {
        final ResponseEntity<DepositChangeResponseDto> response = apiTemplate.exchange("/api/deposit/depositions/{deposit_id}?access_token={token}",
                HttpMethod.PUT, new HttpEntity<>(data), DepositChangeResponseDto.class, id, zenodoConfig.getApiKey());
        if (response.getStatusCode().equals(HttpStatus.UNAUTHORIZED)) {
            throw new ZenodoAuthenticationException("Token is missing or invalid.");
        }
        if (response.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
            throw new ZenodoNotFoundException("Could not get the citation.");
        }
        if (!response.getStatusCode().equals(HttpStatus.OK)) {
            throw new ZenodoAuthenticationException("Could not update the citation.");
        }
        if (response.getBody() == null) {
            throw new ZenodoApiException("Endpoint returned null body");
        }
        return response.getBody();
    }

    @Override
    public void deleteCitation(Long id) throws ZenodoAuthenticationException, ZenodoApiException {
        final ResponseEntity<String> response = apiTemplate.exchange("/api/deposit/depositions/{deposit_id}?access_token={token}",
                HttpMethod.DELETE, null, String.class, id, zenodoConfig.getApiKey());
        if (response.getStatusCode().equals(HttpStatus.UNAUTHORIZED)) {
            throw new ZenodoAuthenticationException("Token is missing or invalid.");
        }
        if (!response.getStatusCode().equals(HttpStatus.CREATED)) {
            throw new ZenodoApiException("Could not delete the deposit");
        }
    }
}
