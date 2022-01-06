package at.tuwien.gateway.impl;

import at.tuwien.api.database.query.QueryDto;
import at.tuwien.exception.QueryNotFoundException;
import at.tuwien.exception.RemoteUnavailableException;
import at.tuwien.gateway.QueryServiceGateway;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class QueryServiceGatewayImpl implements QueryServiceGateway {

    @Override
    public QueryDto find(Long queryId) throws QueryNotFoundException, RemoteUnavailableException {
        if (false) {
            log.error("Query not found with id {}", queryId);
            log.debug("The remote service did not find the query in the metadata database");
            throw new QueryNotFoundException("Query not found");
        } else {
            log.error("Query service not available");
            throw new RemoteUnavailableException("Query service not available");
        }
    }
}
