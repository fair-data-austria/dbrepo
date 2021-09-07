package at.tuwien.service;

import at.tuwien.BaseUnitTest;
import at.tuwien.config.ReadyConfig;
import at.tuwien.entities.database.query.Query;
import at.tuwien.exception.DatabaseConnectionException;
import at.tuwien.exception.DatabaseNotFoundException;
import at.tuwien.exception.ImageNotSupportedException;
import at.tuwien.exception.QueryMalformedException;
import at.tuwien.repository.DatabaseRepository;
import lombok.SneakyThrows;
import net.sf.jsqlparser.JSQLParserException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

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

    @MockBean
    private DatabaseRepository databaseRepository;






    /*
    @Test
    public void findAll_notPostgres_fails() throws DatabaseConnectionException, QueryMalformedException {
        when(databaseRepository.findById(DATABASE_2_ID))
                .thenReturn(Optional.of(DATABASE_2));
        when(postgresService.getQueries(DATABASE_2))
                .thenThrow(QueryMalformedException.class);

        /* test
        assertThrows(QueryMalformedException.class, () -> {
            queryService.findAll(DATABASE_2_ID);
        });
    } */

    @Test
    public void executeStatement_succeeds() {
        //
    }

    @Test
    public void parse_notValidSyntax_fails() throws DatabaseNotFoundException {
        when(queryService.findDatabase(DATABASE_1_ID))
                .thenReturn(DATABASE_1);
        when(databaseRepository.findById(DATABASE_1_ID))
                .thenReturn(Optional.of(DATABASE_1));


        assertThrows(JSQLParserException.class, () -> {
            queryService.execute(DATABASE_1_ID, QUERY_1);
        });
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
