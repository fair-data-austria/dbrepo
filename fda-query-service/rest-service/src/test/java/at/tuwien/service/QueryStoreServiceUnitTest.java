package at.tuwien.service;

import at.tuwien.BaseUnitTest;
import at.tuwien.entities.database.query.Query;
import at.tuwien.exception.DatabaseConnectionException;
import at.tuwien.exception.DatabaseNotFoundException;
import at.tuwien.exception.ImageNotSupportedException;
import at.tuwien.exception.QueryMalformedException;
import at.tuwien.repository.DatabaseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class QueryStoreServiceUnitTest extends BaseUnitTest {

    @Autowired
    private QueryStoreService queryStoreService;

    @MockBean
    private DatabaseRepository databaseRepository;
    /*
    @Test
    public void findAll_succeeds() throws DatabaseNotFoundException, ImageNotSupportedException,
            DatabaseConnectionException, QueryMalformedException, SQLException {
        when(databaseRepository.findById(DATABASE_1_ID))
                .thenReturn(Optional.of(DATABASE_1));
        when(queryStoreService.findAll(DATABASE_1_ID))
                .thenReturn(List.of(QUERY_1));

        // test
        final List<Query> response = queryStoreService.findAll(DATABASE_1_ID);
        assertEquals(1, response.size());
        assertEquals(QUERY_1_ID, response.get(0).getId());
    }
*/
}
