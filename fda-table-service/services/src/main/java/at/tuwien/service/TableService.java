package at.tuwien.service;

import at.tuwien.dto.table.TableCreateDto;
import at.tuwien.entity.Container;
import at.tuwien.entity.Database;
import at.tuwien.entity.Table;
import at.tuwien.exception.DatabaseConnectionException;
import at.tuwien.exception.ImageNotSupportedException;
import at.tuwien.exception.TableMalformedException;
import at.tuwien.mapper.TableMapper;
import at.tuwien.repository.ContainerRepository;
import at.tuwien.repository.DatabaseRepository;
import at.tuwien.repository.TableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TableService {

    private final TableMapper tableMapper;
    private final TableRepository tableRepository;
    private final DatabaseRepository databaseRepository;
    private final ContainerRepository containerRepository;
    private final PostgresService postgresService;

    @Autowired
    public TableService(TableMapper tableMapper, TableRepository tableRepository,
                        DatabaseRepository databaseRepository, ContainerRepository containerRepository,
                        PostgresService postgresService) {
        this.tableMapper = tableMapper;
        this.tableRepository = tableRepository;
        this.databaseRepository = databaseRepository;
        this.containerRepository = containerRepository;
        this.postgresService = postgresService;
    }

    public List<Table> findAll(Long databaseId) {
        final Database tmp = new Database();
        tmp.setId(databaseId);
        return tableRepository.findByDatabase(tmp);
    }

    public List<Table> findById(Long databaseId, Long tableId) {
        final Database tmp = new Database();
        tmp.setId(databaseId);
        return tableRepository.findByDatabaseAndId(tmp, tableId);
    }

    public Table create(Long databaseId, TableCreateDto createDto) throws ImageNotSupportedException,
            DatabaseConnectionException, TableMalformedException {
        final Database database = databaseRepository.getOne(databaseId);
        final Table table = tableMapper.tableCreateDtoToTable(createDto);
        database.getTables()
                .add(table);
        // get container
        final Container container = containerRepository.findContainerByContainerId(database.getContainerId());
        // add to database depending on type, todo currently only postgres
        if (container.getImage().getRepository().equals("postgres")) {
            postgresService.createTable(container, database, createDto);
        } else {
            throw new ImageNotSupportedException("Currently only PostgreSQL is supported");
        }
        databaseRepository.save(database);
        return table;
    }

}
