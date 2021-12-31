package at.tuwien.service;

import at.tuwien.api.database.query.ExecuteQueryDto;
import at.tuwien.api.database.query.QueryResultDto;
import at.tuwien.exception.*;

public interface QueryService {

    /**
     * Executes a query on a given database id and table id
     *
     * @param databaseId The database id.
     * @param tableId    The table id.
     * @param query      The query.
     * @return The result of the query if successful
     */
    QueryResultDto execute(Long databaseId, Long tableId, ExecuteQueryDto query) throws DatabaseNotFoundException,
            ImageNotSupportedException, QueryMalformedException, TableNotFoundException;
}
