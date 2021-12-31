package at.tuwien.service;

import at.tuwien.api.database.table.TableCsvDto;
import at.tuwien.exception.DatabaseConnectionException;
import at.tuwien.exception.DatabaseNotFoundException;
import at.tuwien.exception.ImageNotSupportedException;
import at.tuwien.exception.TableNotFoundException;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.time.Instant;

public interface FileService {
    TableCsvDto read(Long databaseId, Long tableId, String location) throws IOException, CsvException,
            TableNotFoundException, DatabaseNotFoundException;

    TableCsvDto read(Long databaseId, Long tableId, String location, Character separator, Boolean skipHeader, String nullElement,
                     String falseElement, String trueElement) throws IOException, CsvException,
            TableNotFoundException, DatabaseNotFoundException;

    Resource write(Long databaseId, Long tableId, Instant timestamp) throws TableNotFoundException,
            DatabaseConnectionException, TableMalformedException, DatabaseNotFoundException, ImageNotSupportedException,
            PaginationException, FileStorageException;

    Resource write(Long databaseId, Long tableId) throws TableNotFoundException, DatabaseConnectionException,
            TableMalformedException, DatabaseNotFoundException, ImageNotSupportedException, PaginationException, FileStorageException;
}
