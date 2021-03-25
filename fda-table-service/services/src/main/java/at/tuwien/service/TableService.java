package at.tuwien.service;

import at.tuwien.dto.table.TableBriefDto;
import at.tuwien.dto.table.TableDto;
import at.tuwien.entity.Database;
import at.tuwien.mapper.TableMapper;
import at.tuwien.repository.TableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TableService {

    private final TableRepository tableRepository;
    private final TableMapper tableMapper;

    @Autowired
    public TableService(TableRepository tableRepository, TableMapper tableMapper) {
        this.tableRepository = tableRepository;
        this.tableMapper = tableMapper;
    }

    public List<TableBriefDto> findAll(Long databaseId) {
        final Database database = new Database();
        database.setId(databaseId);
        return tableRepository.findByDatabase(database)
                .stream()
                .map(tableMapper::tableToTableBriefDto)
                .collect(Collectors.toList());
    }

    public List<TableDto> findById(Long databaseId, Long tableId) {
        final Database database = new Database();
        database.setId(databaseId);
        return tableRepository.findByDatabaseAndId(database, tableId)
                .stream()
                .map(tableMapper::tableToTableDto)
                .collect(Collectors.toList());
    }

}
