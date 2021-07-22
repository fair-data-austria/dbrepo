package at.tuwien.service;

import at.tuwien.api.database.query.QueryResultDto;
import at.tuwien.entities.database.Database;
import at.tuwien.entities.database.query.Query;
import at.tuwien.exception.DatabaseConnectionException;
import at.tuwien.exception.DatabaseNotFoundException;
import at.tuwien.exception.ImageNotSupportedException;
import at.tuwien.exception.QueryMalformedException;
import at.tuwien.mapper.ImageMapper;
import at.tuwien.mapper.QueryMapper;
import at.tuwien.repository.DatabaseRepository;
import lombok.extern.log4j.Log4j2;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectItem;
import org.jooq.DSLContext;
import org.junit.jupiter.params.shadow.com.univocity.parsers.common.DataProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.StringReader;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import static org.jooq.impl.DSL.constraint;
import static org.jooq.impl.SQLDataType.*;

@Log4j2
@Service
public class QueryService extends JdbcConnector {

    private final DatabaseRepository databaseRepository;


    @Autowired
    public QueryService(ImageMapper imageMapper, QueryMapper queryMapper, DatabaseRepository databaseRepository) {
        super(imageMapper, queryMapper);
        this.databaseRepository = databaseRepository;
    }

    public QueryResultDto executeStatement(Long id, Query query) throws ImageNotSupportedException, DatabaseNotFoundException, JSQLParserException, SQLFeatureNotSupportedException {
        CCJSqlParserManager parserRealSql = new CCJSqlParserManager();

        Statement stmt = parserRealSql.parse(new StringReader(query.getQuery()));
        if(stmt instanceof Select) {
            Select selectStatement = (Select) stmt;
            PlainSelect ps = (PlainSelect)selectStatement.getSelectBody();

            List<SelectItem> selectitems = ps.getSelectItems();
            System.out.println(ps.getFromItem().toString());
            selectitems.stream().forEach(selectItem -> System.out.println(selectItem.toString()));
        }
        else {
            throw new SQLFeatureNotSupportedException("SQL Query is not a SELECT statement - please only use SELECT statements");
        }
        //saveQuery(database, query, null);

        return null;
    }


}
