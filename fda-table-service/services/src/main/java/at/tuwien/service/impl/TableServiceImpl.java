package at.tuwien.service.impl;

import at.tuwien.api.database.table.TableCreateDto;
import at.tuwien.api.database.table.TableCsvDto;
import at.tuwien.api.database.table.TableInsertDto;
import at.tuwien.entities.database.Database;
import at.tuwien.entities.database.table.Table;
import at.tuwien.exception.*;
import at.tuwien.repository.jpa.DatabaseRepository;
import at.tuwien.service.TableService;
import com.opencsv.exceptions.CsvException;
import lombok.extern.log4j.Log4j2;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;

@Log4j2
@Service
public class TableServiceImpl extends HibernateConnector implements TableService {

    private final DatabaseRepository databaseRepository;

    @Autowired
    public TableServiceImpl(DatabaseRepository databaseRepository) {
        this.databaseRepository = databaseRepository;
    }

    @Override
    public List<Table> findAll() {
        return null;
    }

    @Override
    public List<Table> findAllForDatabaseId(Long databaseId) throws DatabaseNotFoundException {
        return null;
    }

    @Override
    public void deleteTable(Long databaseId, Long tableId) throws TableNotFoundException, DatabaseNotFoundException, ImageNotSupportedException, DataProcessingException {

    }

    @Override
    public Table findById(Long databaseId, Long tableId) throws TableNotFoundException, DatabaseNotFoundException {
        return null;
    }

    @Override
    @Transactional
    public Table createTable(Long databaseId, TableCreateDto createDto) throws ImageNotSupportedException, DatabaseNotFoundException, DataProcessingException, ArbitraryPrimaryKeysException, TableMalformedException {
        final Database database = find(databaseId);
        final Session session = getSessionFactory(database).openSession();
        final Transaction transaction = session.beginTransaction();
        final Query<Void> query = session.createQuery("", Void.class);
        query.executeUpdate();
        transaction.commit();
        session.close();
        return null;
    }

    @Override
    public Database findDatabase(Long id) throws DatabaseNotFoundException {
        return null;
    }

    @Override
    public TableCsvDto readCsv(Table table, TableInsertDto data, MultipartFile file) throws IOException, CsvException, ArrayIndexOutOfBoundsException {
        return null;
    }

    @Transactional
    protected Database find(Long databaseId) throws DatabaseNotFoundException {
        final Optional<Database> optional = databaseRepository.findById(databaseId);
        if (optional.isEmpty()) {
            log.error("Failed to find database with id {} in metadata database", databaseId);
            throw new DatabaseNotFoundException("Database not found");
        }
        return optional.get();
    }
}
