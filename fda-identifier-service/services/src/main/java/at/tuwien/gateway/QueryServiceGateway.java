package at.tuwien.gateway;

import at.tuwien.api.database.query.QueryDto;
import at.tuwien.exception.QueryNotFoundException;
import at.tuwien.exception.RemoteUnavailableException;
import org.springframework.stereotype.Service;

@Service
public interface QueryServiceGateway {

    /**
     * Finds a query by given id from the query service that internally looks in the query store of a container.
     *
     * @param queryId The query id.
     * @return The query information if successful.
     * @throws QueryNotFoundException     The query was not found.
     * @throws RemoteUnavailableException The remote service is not available.
     */
    QueryDto find(Long queryId) throws QueryNotFoundException, RemoteUnavailableException;
}
