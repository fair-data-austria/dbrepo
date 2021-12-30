package at.tuwien.service;

import at.tuwien.api.database.table.TableCsvDto;
import at.tuwien.api.database.table.TableInsertDto;
import at.tuwien.exception.DatabaseNotFoundException;
import at.tuwien.exception.TableNotFoundException;
import com.opencsv.exceptions.CsvException;

import java.io.IOException;

public interface TextDataService {

    /**
     * Reads a file and metadata into our data structure for a given table id
     *
     * @param databaseId The database id.
     * @param tableId    The table id.
     * @param data       The metadata.
     * @return Our data structure with data in it.
     * @throws IOException  File not readable.
     * @throws CsvException Problem with parsing the csv.
     */
    TableCsvDto read(Long databaseId, Long tableId, TableInsertDto data) throws IOException, CsvException, TableNotFoundException, DatabaseNotFoundException;
}
