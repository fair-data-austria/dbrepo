package at.tuwien.service;

import at.tuwien.api.database.query.QueryResultDto;
import at.tuwien.api.database.table.TableCreateDto;
import at.tuwien.entities.database.Database;
import at.tuwien.entities.database.table.Table;
import at.tuwien.exception.DataProcessingException;
import at.tuwien.exception.DatabaseConnectionException;
import at.tuwien.exception.TableMalformedException;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

public interface ContainerDatabaseService {

    Connection getConnection(Database database) throws DatabaseConnectionException;

    void createTable(Database database, TableCreateDto tableSpecification) throws DatabaseConnectionException, TableMalformedException, DataProcessingException;

    QueryResultDto insertIntoTable(Database database, Table table, List<Map<String, Object>> data, List<String> headers) throws DatabaseConnectionException, DataProcessingException;

    QueryResultDto getAllRows(Database database, Table table) throws DatabaseConnectionException, DataProcessingException;

    void deleteTable(Table table) throws DatabaseConnectionException, TableMalformedException, DataProcessingException;
}
