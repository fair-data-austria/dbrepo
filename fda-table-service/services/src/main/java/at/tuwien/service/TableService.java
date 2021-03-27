package at.tuwien.service;

import at.tuwien.dto.table.TableCreateDto;
import at.tuwien.entity.Database;
import at.tuwien.entity.Table;
import at.tuwien.exception.DatabaseConnectionException;
import at.tuwien.exception.DatabaseNotFoundException;
import at.tuwien.exception.ImageNotSupportedException;
import at.tuwien.exception.TableMalformedException;
import at.tuwien.repository.DatabaseRepository;
import at.tuwien.repository.TableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
public class TableService {

    private final TableRepository tableRepository;
    private final DatabaseRepository databaseRepository;
    private final PostgresService postgresService;

    @Autowired
    public TableService(TableRepository tableRepository, DatabaseRepository databaseRepository,
                        PostgresService postgresService) {
        this.tableRepository = tableRepository;
        this.databaseRepository = databaseRepository;
        this.postgresService = postgresService;
    }

    public List<Table> findAll(Long databaseId) throws DatabaseNotFoundException {
        final Database tmp = new Database();
        tmp.setId(databaseId);
        final List<Table> tables;
        try {
            tables = tableRepository.findByDatabase(tmp);
        } catch (EntityNotFoundException e) {
            throw new DatabaseNotFoundException("database was not found");
        }
        return tables;
    }

    public List<Table> findById(Long databaseId, Long tableId) {
        final Database tmp = new Database();
        tmp.setId(databaseId);
        return tableRepository.findByDatabaseAndId(tmp, tableId);
    }

    public Table create(Long databaseId, TableCreateDto createDto) throws ImageNotSupportedException,
            DatabaseConnectionException, TableMalformedException, DatabaseNotFoundException {
        final Database database;
        try {
            database = databaseRepository.getOne(databaseId);
        } catch (EntityNotFoundException e) {
            throw new DatabaseNotFoundException("database not found in metadata database", e);
        }
        final Table table;
        if (database.getContainer().getImage().getRepository().equals("postgres")) {
            table = postgresService.createTable(database, createDto);
        } else {
            throw new ImageNotSupportedException("Currently only PostgreSQL is supported");
        }
        return table;
    }

}
