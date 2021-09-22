package at.tuwien.gateway;

import at.tuwien.api.zenodo.deposit.DepositDto;
import at.tuwien.config.ZenodoConfig;
import at.tuwien.exception.ZenodoApiException;
import at.tuwien.exception.ZenodoAuthenticationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Component
public class ZenodoGateway {

    private final RestTemplate zenodoRestTemplate;
    private final ZenodoConfig zenodoConfig;

    @Autowired
    public ZenodoGateway(RestTemplate zenodoRestTemplate, ZenodoConfig zenodoConfig) {
        this.zenodoRestTemplate = zenodoRestTemplate;
        this.zenodoConfig = zenodoConfig;
    }

    public List<DepositDto> listDeposits() throws ZenodoAuthenticationException, ZenodoApiException {
        final ResponseEntity<DepositDto[]> response = zenodoRestTemplate.exchange("/api/deposit/depositions?access_token={token}",
                HttpMethod.GET, null, DepositDto[].class, zenodoConfig.getZenodoApiKey());
        if (response.getStatusCode().is4xxClientError()) {
            throw new ZenodoAuthenticationException("Token is missing or invalid.");
        }
        if (response.getBody() == null) {
            throw new ZenodoApiException("Endpoint returned null body");
        }
        if (response.getBody().length == 0) {
            throw new ZenodoApiException("Endpoint returned empty body");
        }
        return Arrays.asList(response.getBody());
    }
}
