package at.tuwien.service;

import at.tuwien.entities.database.table.Table;
import at.tuwien.exception.DatabaseNotFoundException;
import at.tuwien.exception.TableNotFoundException;

public interface TableService {

    /**
     * Find a table in the metadata database by database-table id tuple
     *
     * @param databaseId The database id.
     * @param tableId    The table id.
     * @return The database.
     * @throws DatabaseNotFoundException The database is not found.
     */
    Table find(Long databaseId, Long tableId) throws DatabaseNotFoundException, TableNotFoundException;
}
