package at.tuwien.service;

import at.tuwien.BaseUnitTest;
import at.tuwien.config.ReadyConfig;
import at.tuwien.entities.database.query.Query;
import at.tuwien.exception.DatabaseConnectionException;
import at.tuwien.exception.DatabaseNotFoundException;
import at.tuwien.exception.ImageNotSupportedException;
import at.tuwien.exception.QueryMalformedException;
import lombok.SneakyThrows;
import net.sf.jsqlparser.JSQLParserException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static java.lang.Boolean.TRUE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class QueryServiceUnitTest extends BaseUnitTest {

    @MockBean
    private ReadyConfig readyConfig;

    @Autowired
    private QueryService queryService;

    @Autowired
    private QueryStoreService queryStoreService;

    @Test
    public void executeStatement_succeeds() {
        //
    }

    @Test
    public void execute_notValidSyntax_fails() throws ImageNotSupportedException, DatabaseConnectionException {
        //
    }

    @Test
    public void executeStatement_notFound_fails() {
        //
    }

    @Test
    public void create_succeeds() {
        //
    }

    @Test
    public void create_notSql_fails() {
        //
    }


}
