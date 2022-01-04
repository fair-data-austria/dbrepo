package at.tuwien.service;

import at.tuwien.api.database.table.TableCsvDto;
import at.tuwien.exception.*;
import com.opencsv.exceptions.CsvException;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.time.Instant;

public interface DataService {
    TableCsvDto read(Long databaseId, Long tableId, String location) throws IOException, CsvException,
            TableNotFoundException, DatabaseNotFoundException;

    TableCsvDto read(Long databaseId, Long tableId, String location, Character separator, Boolean skipHeader, String nullElement,
                     String falseElement, String trueElement) throws IOException,
            TableNotFoundException, DatabaseNotFoundException, CsvException;

    Resource write(Long databaseId, Long tableId, Instant timestamp) throws TableNotFoundException,
            DatabaseConnectionException, DatabaseNotFoundException, ImageNotSupportedException,
            PaginationException, FileStorageException, TableMalformedException;

    Resource write(Long databaseId, Long tableId) throws TableNotFoundException, DatabaseConnectionException,
            TableMalformedException, DatabaseNotFoundException, ImageNotSupportedException, PaginationException, FileStorageException;
}
