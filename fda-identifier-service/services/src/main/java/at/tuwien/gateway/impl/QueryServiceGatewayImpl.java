package at.tuwien.gateway.impl;

import at.tuwien.api.database.query.QueryDto;
import at.tuwien.api.identifier.IdentifierDto;
import at.tuwien.exception.QueryNotFoundException;
import at.tuwien.exception.RemoteUnavailableException;
import at.tuwien.gateway.QueryServiceGateway;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class QueryServiceGatewayImpl implements QueryServiceGateway {

    private final RestTemplate restTemplate;

    @Autowired
    public QueryServiceGatewayImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public QueryDto find(IdentifierDto identifier) throws QueryNotFoundException, RemoteUnavailableException {
        final String url = "/api/database/" + identifier.getDbid() + "/query/" + identifier.getQid();
        final ResponseEntity<QueryDto> response;
        try {
            response = restTemplate.getForEntity(url, QueryDto.class);
        } catch (ResourceAccessException | HttpServerErrorException.ServiceUnavailable e) {
            log.error("Query service not available");
            log.debug("service not available for identifier {}", identifier);
            throw new RemoteUnavailableException("Query service not available");
        }
        if (response.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
            log.error("Query not found with id {}", identifier.getQid());
            log.debug("query not found for identifier {}", identifier);
            throw new QueryNotFoundException("Query not found");
        }
        return response.getBody();
    }
}
