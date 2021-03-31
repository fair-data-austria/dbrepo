package at.tuwien.endpoint;

import at.tuwien.dto.container.ContainerDto;
import at.tuwien.config.RestClient;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class ContainerGateway {

    /** @apiNote this url is already in Eureka and NOT docker */
    private static final String URL = "http://fda-container-service/api/container/";

    private final RestClient restClient;

    @Autowired
    public ContainerGateway(RestClient restClient) {
        this.restClient = restClient;
    }

    public ContainerDto inspect(Long id) {
        final ResponseEntity<ContainerDto> response;
        response = restClient.exchange(URL + id, HttpMethod.GET, null, new ParameterizedTypeReference<>() {
            });
        return response.getBody();
    }

}
