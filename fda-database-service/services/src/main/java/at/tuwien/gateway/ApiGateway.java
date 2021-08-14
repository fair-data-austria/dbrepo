package at.tuwien.gateway;

import at.tuwien.api.amqp.DataDto;
import at.tuwien.api.amqp.TupleDto;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Log4j2
@Component
public class ApiGateway {

    private final RestTemplate restTemplate;

    @Autowired
    public ApiGateway(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void insert(Integer databaseId, Integer tableId, TupleDto[] data) {
        final String url = "http://fda-gateway-service/api/database/" + databaseId + "/table/" + tableId;
        log.debug("insert AMPQ data {}", Arrays.asList(data));
        final ResponseEntity<?> response = restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(data),
                new ParameterizedTypeReference<List<Long>>() {
                });
    }
}
