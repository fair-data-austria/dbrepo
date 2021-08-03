package at.tuwien.service;

import at.tuwien.api.database.query.QueryResultDto;
import at.tuwien.entities.database.Database;
import at.tuwien.entities.database.query.Query;
import at.tuwien.entities.database.table.Table;
import at.tuwien.entities.database.table.columns.TableColumn;
import at.tuwien.exception.*;
import at.tuwien.mapper.ImageMapper;
import at.tuwien.mapper.QueryMapper;
import at.tuwien.repository.DatabaseRepository;
import lombok.extern.log4j.Log4j2;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.*;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.ResultQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.StringReader;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.jooq.impl.DSL.constraint;

@Log4j2
@Service
public class QueryService extends JdbcConnector {

    private final DatabaseRepository databaseRepository;
    private final QueryStoreService queryStoreService;
    private final QueryMapper queryMapper;

    @Autowired
    public QueryService(ImageMapper imageMapper, QueryMapper queryMapper, DatabaseRepository databaseRepository, QueryStoreService queryStoreService) {
        super(imageMapper, queryMapper);
        this.databaseRepository = databaseRepository;
        this.queryStoreService = queryStoreService;
        this.queryMapper = queryMapper;
    }

    @Transactional
    public QueryResultDto execute(Long id, Query query) throws ImageNotSupportedException, DatabaseNotFoundException, JSQLParserException, SQLException, QueryMalformedException, QueryStoreException {
        Database database = findDatabase(id);
        if(database.getContainer().getImage().getDialect().equals("MARIADB")){
            if(!queryStoreService.exists(database)) {
                queryStoreService.create(id);
            }
        }
        DSLContext context = open(database);
        //TODO Fix that import
        ResultQuery<Record> resultQuery = context.resultQuery(parse(query,database).getQuery());
        Result<Record> result = resultQuery.fetch();
        QueryResultDto queryResultDto = queryMapper.recordListToQueryResultDto(result);
        log.debug(result.toString());

        // Save the query in the store
        queryStoreService.saveQuery(database, query, queryResultDto);
        return queryResultDto;
    }

    private Query parse(Query query, Database database) throws SQLException, ImageNotSupportedException, JSQLParserException {
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        query.setExecutionTimestamp(ts);

        CCJSqlParserManager parserRealSql = new CCJSqlParserManager();
        Statement statement = parserRealSql.parse(new StringReader(query.getQuery()));
        log.debug("Given query {}", query.getQuery());

        if(statement instanceof Select) {
            Select selectStatement = (Select) statement;
            PlainSelect ps = (PlainSelect)selectStatement.getSelectBody();
            List<SelectItem> selectItems = ps.getSelectItems();

            //Parse all tables
            List<FromItem> fromItems = new ArrayList<>();
            fromItems.add(ps.getFromItem());
            if(ps.getJoins() != null && ps.getJoins().size() > 0) {
                for (Join j : ps.getJoins()) {
                    if (j.getRightItem() != null) {
                        fromItems.add(j.getRightItem());
                    }
                }
            }
            //Checking if all tables exist
            List<TableColumn> allColumns = new ArrayList<>();
            for(FromItem f : fromItems) {
                boolean i = false;
                log.debug("from item iterated through: {}", f);
                for(Table t : database.getTables()) {
                    if(f.toString().equals(t.getInternalName()) || f.toString().equals(t.getName())) {
                        allColumns.addAll(t.getColumns());
                        i=false;
                        break;
                    }
                    i = true;
                }
                if(i) {
                    throw new JSQLParserException("Table "+f.toString() + " does not exist");
                }
            }

            //Checking if all columns exist
            for(SelectItem s : selectItems) {
                String select = s.toString();
                if(select.trim().equals("*")) {
                    log.debug("Please do not use * to query data");
                    continue;
                }
                if(select.contains(".")) {
                    log.debug(select);
                    select = select.split("\\.")[1];
                }
                boolean i = false;
                for(TableColumn tc : allColumns ) {
                    log.debug("{},{},{}", tc.getInternalName(), tc.getName(), s);
                    if(select.equals(tc.getInternalName()) || select.toString().equals(tc.getName())) {
                        i=false;
                        break;
                    }
                    i = true;
                }
                if(i) {
                    throw new JSQLParserException("Column "+s.toString() + " does not exist");
                }
            }

            PlainSelect result = new PlainSelect();
            result.setSelectItems(ps.getSelectItems());

            result.setFromItem(ps.getFromItem());
            //TODO extract from as a whole?
            if(ps.getJoins()!=null) {
                result.setJoins(ps.getJoins());
            }

            if(ps.getWhere() != null) {
                Expression where = ps.getWhere();
                result.setWhere(where);

            }
            selectItems.stream().forEach(selectItem -> System.out.println(selectItem.toString()));
            query.setQueryNormalized(result.toString());
            return query;
        }
        else {
            throw new JSQLParserException("SQL Query is not a SELECT statement - please only use SELECT statements");
        }

    }

    @Transactional
    public Database findDatabase(Long id) throws DatabaseNotFoundException {
        final Optional<Database> database = databaseRepository.findById(id);
        if (database.isEmpty()) {
            log.error("no database with this id found in metadata database");
            throw new DatabaseNotFoundException("database not found in metadata database");
        }
        return database.get();
    }




}
