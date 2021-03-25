package at.tuwien.service;

import at.tuwien.dto.table.TableCreateDto;
import at.tuwien.entity.Database;
import at.tuwien.entity.Table;
import at.tuwien.mapper.TableMapper;
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

    @Autowired
    public TableService(TableMapper tableMapper, TableRepository tableRepository,
                        DatabaseRepository databaseRepository) {
        this.tableMapper = tableMapper;
        this.tableRepository = tableRepository;
        this.databaseRepository = databaseRepository;
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

    public Table create(Long databaseId, TableCreateDto createDto) {
        final Database database = databaseRepository.getOne(databaseId);
        final Table table = tableMapper.tableCreateDtoToTable(createDto);
        database.getTables()
                .add(table);
        databaseRepository.save(database);
        return table;
    }

}
