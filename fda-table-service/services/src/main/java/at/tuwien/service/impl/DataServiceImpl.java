package at.tuwien.service.impl;

import at.tuwien.api.database.query.QueryResultDto;
import at.tuwien.api.database.table.TableCsvDto;
import at.tuwien.api.database.table.TableInsertDto;
import at.tuwien.entities.database.table.Table;
import at.tuwien.exception.*;
import at.tuwien.service.DataService;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class DataServiceImpl implements DataService {
    @Override
    public Table findById(Long databaseId, Long tableId) throws TableNotFoundException, DatabaseNotFoundException {
        return null;
    }

    @Override
    public void insertCsv(Long databaseId, Long tableId, TableInsertDto data) throws TableNotFoundException, ImageNotSupportedException, DatabaseNotFoundException, FileStorageException, TableMalformedException {

    }

    @Override
    public void insert(Table table, TableCsvDto data) throws ImageNotSupportedException, TableMalformedException {

    }

    @Override
    public QueryResultDto selectAll(@NonNull Long databaseId, @NonNull Long tableId, Instant timestamp, Long page, Long size) throws TableNotFoundException, DatabaseNotFoundException, ImageNotSupportedException, DatabaseConnectionException, TableMalformedException {
        return null;
    }
}
