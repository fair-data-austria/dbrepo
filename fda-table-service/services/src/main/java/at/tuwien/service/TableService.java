package at.tuwien.service;

import at.tuwien.dto.table.TableCreateDto;
import at.tuwien.entity.Database;
import at.tuwien.entity.Table;
import at.tuwien.exception.DatabaseConnectionException;
import at.tuwien.exception.DatabaseNotFoundException;
import at.tuwien.exception.ImageNotSupportedException;
import at.tuwien.exception.TableMalformedException;
import at.tuwien.mapper.TableMapper;
import at.tuwien.repository.DatabaseRepository;
import at.tuwien.repository.TableRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Log4j2
@Service
public class TableService {

    private final TableRepository tableRepository;
    private final DatabaseRepository databaseRepository;
    private final PostgresService postgresService;
    private final TableMapper tableMapper;

    @Autowired
    public TableService(TableRepository tableRepository, DatabaseRepository databaseRepository,
                        PostgresService postgresService, TableMapper tableMapper) {
        this.tableRepository = tableRepository;
        this.databaseRepository = databaseRepository;
        this.postgresService = postgresService;
        this.tableMapper = tableMapper;
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
        final Optional<Database> database;
        try {
            database = databaseRepository.findById(databaseId);
        } catch (EntityNotFoundException e) {
            log.error("database not found in metadata database");
            throw new DatabaseNotFoundException("database not found in metadata database", e);
        }
        if (database.isEmpty()) {
            log.error("no database with this id found in metadata database");
            throw new DatabaseNotFoundException("database not found in metadata database");
        }
        log.debug("retrieved db {}", database);
        if (!database.get().getContainer().getImage().getRepository().equals("postgres")) {
            log.error("Right now only PostgreSQL is supported!");
            throw new ImageNotSupportedException("Currently only PostgreSQL is supported");
        }
        /* save in metadata db */
        postgresService.createTable(database.get(), createDto);
        final Table table = tableMapper.tableCreateDtoToTable(createDto);
        table.setDatabase(database.get());
        table.setInternalName(tableMapper.columnNameToString(table.getName()));
        log.debug("about to save table {}", table);
        final Table out = persistTable(table);
        log.debug("saved table {}", out);
        log.info("Created table {} in database {}", out.getId(), out.getDatabase().getId());
        return out;
    }

    @Transactional
    protected Table persistTable(Table table) {
        return tableRepository.save(table);
    }

}
