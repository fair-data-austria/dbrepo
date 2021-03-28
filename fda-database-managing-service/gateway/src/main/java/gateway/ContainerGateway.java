package gateway;

import at.tuwien.dto.container.ContainerDto;
import config.RestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class ContainerGateway {

    // TODO Eureka?
    private static final String URL = "http://fda-container-service:9091/api/container/";

    private final RestClient restClient;

    @Autowired
    public ContainerGateway(RestClient restClient) {
        this.restClient = restClient;
    }

    public ContainerDto inspect(Long id) {
        final ResponseEntity<ContainerDto> response = restClient.exchange(URL + id,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                });
        return response.getBody();
    }

}
