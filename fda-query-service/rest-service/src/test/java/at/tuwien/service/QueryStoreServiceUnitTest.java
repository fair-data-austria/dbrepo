package at.tuwien.service;

import at.tuwien.BaseUnitTest;
import at.tuwien.exception.*;
import at.tuwien.repository.jpa.DatabaseRepository;
import at.tuwien.service.impl.StoreServiceImpl;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.sql.SQLException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class QueryStoreServiceUnitTest extends BaseUnitTest {

    @Autowired
    private StoreServiceImpl queryStoreService;

    @Autowired
    private QueryService queryService;

    @MockBean
    private DatabaseRepository databaseRepository;

    /*
    @Test
    public void findAll_succeeds() throws DatabaseNotFoundException, ImageNotSupportedException,
            DatabaseConnectionException, QueryMalformedException, SQLException, QueryStoreException {
        when(databaseRepository.findById(DATABASE_1_ID))
                .thenReturn(Optional.of(DATABASE_1));
        when(queryStoreService.findAll(DATABASE_1_ID))
                .thenReturn(List.of(QUERY_1));

        // test
        final List<Query> response = queryStoreService.findAll(DATABASE_1_ID);
        assertEquals(1, response.size());
        assertEquals(QUERY_1_ID, response.get(0).getId());
    } */


    //@Test
    public void findAll_notFound_fails() {
        when(databaseRepository.findById(DATABASE_1_ID))
                .thenReturn(Optional.empty());

        // test
        assertThrows(DatabaseNotFoundException.class, () -> {
            queryStoreService.findAll(DATABASE_1_ID);
        });
    }


    //@Test
    public void findAll_noConnection_fails() throws DatabaseConnectionException, QueryMalformedException, SQLException, DatabaseNotFoundException, ImageNotSupportedException {
        when(databaseRepository.findById(DATABASE_1_ID))
                .thenReturn(Optional.of(DATABASE_1));

        /* test */
        assertThrows(DatabaseConnectionException.class, () -> {
            queryStoreService.findAll(DATABASE_2_ID);
        });
    }


}
