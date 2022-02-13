package at.tuwien.service;

import at.tuwien.api.database.table.TableCsvDto;
import at.tuwien.entities.database.table.Table;
import at.tuwien.exception.*;
import org.springframework.stereotype.Service;

@Service
public interface QueryService {

    /**
     * Insert data from stream client into a table of a table-database id tuple, we need the "root" role for this as the
     * default "mariadb" user is configured to only be allowed to execute "SELECT" statements.
     *
     * @param table The table
     * @param data  The data.
     * @throws ImageNotSupportedException The image is not supported.
     * @throws TableMalformedException    The table does not exist in the metadata database.
     * @throws DatabaseNotFoundException  The database is not found in the metadata database.
     * @throws TableNotFoundException     The table is not found in the metadata database.
     */
    void insert(Table table, TableCsvDto data) throws ImageNotSupportedException,
            TableMalformedException, DatabaseNotFoundException, TableNotFoundException, ContainerNotFoundException;

}
