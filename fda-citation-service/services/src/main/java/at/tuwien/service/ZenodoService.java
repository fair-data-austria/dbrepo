package at.tuwien.service;

import at.tuwien.api.zenodo.deposit.DepositDto;
import at.tuwien.config.ZenodoConfig;
import at.tuwien.exception.ZenodoApiException;
import at.tuwien.exception.ZenodoAuthenticationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
public class ZenodoService implements CitationService {

    private final RestTemplate zenodoRestTemplate;
    private final ZenodoConfig zenodoConfig;

    @Autowired
    public ZenodoService(RestTemplate zenodoRestTemplate, ZenodoConfig zenodoConfig) {
        this.zenodoRestTemplate = zenodoRestTemplate;
        this.zenodoConfig = zenodoConfig;
    }

    @Override
    public List<DepositDto> listCitations() throws ZenodoAuthenticationException, ZenodoApiException {
        final ResponseEntity<DepositDto[]> response = zenodoRestTemplate.exchange("/api/deposit/depositions?access_token={token}",
                HttpMethod.GET, null, DepositDto[].class, zenodoConfig.getApiKey());
        if (response.getStatusCode().equals(HttpStatus.UNAUTHORIZED)) {
            throw new ZenodoAuthenticationException("Token is missing or invalid.");
        }
        if (response.getBody() == null) {
            throw new ZenodoApiException("Endpoint returned null body");
        }
        return Arrays.asList(response.getBody());
    }

    @Override
    public DepositDto storeCitation() throws ZenodoAuthenticationException, ZenodoApiException {
        final ResponseEntity<DepositDto> response = zenodoRestTemplate.exchange("/api/deposit/depositions?access_token={token}",
                HttpMethod.POST, new HttpEntity<>("{}"), DepositDto.class, zenodoConfig.getApiKey());
        if (response.getStatusCode().equals(HttpStatus.UNAUTHORIZED)) {
            throw new ZenodoAuthenticationException("Token is missing or invalid.");
        }
        if (response.getBody() == null) {
            throw new ZenodoApiException("Endpoint returned null body");
        }
        if (response.getBody().getState().equals("error")) {
            throw new ZenodoApiException("Status returned error or is unknown");
        }
        return response.getBody();
    }

    @Override
    public DepositDto deleteCitation(Long id) throws ZenodoAuthenticationException, ZenodoApiException {
        final ResponseEntity<DepositDto> response = zenodoRestTemplate.exchange("/api/deposit/depositions/{deposit_id}?access_token={token}",
                HttpMethod.POST, null, DepositDto.class, id, zenodoConfig.getApiKey());
        if (response.getStatusCode().equals(HttpStatus.UNAUTHORIZED)) {
            throw new ZenodoAuthenticationException("Token is missing or invalid.");
        }
        if (response.getBody() == null) {
            throw new ZenodoApiException("Endpoint returned null body");
        }
        if (response.getBody().getState().equals("error")) {
            throw new ZenodoApiException("Status returned error or is unknown");
        }
        return response.getBody();
    }
}
