package at.tuwien.service;

import at.tuwien.entities.database.table.Table;
import at.tuwien.exception.FileStorageException;
import at.tuwien.exception.ImageNotSupportedException;

public interface CommaValueService {

    /**
     * Replace null, true and false values within a CSV
     *
     * @param table    The table metadata
     * @param location The CSV location
     * @throws FileStorageException The CSV could not be opened, replaced or saved.
     */
    void replace(Table table, String location) throws FileStorageException, ImageNotSupportedException;
}
