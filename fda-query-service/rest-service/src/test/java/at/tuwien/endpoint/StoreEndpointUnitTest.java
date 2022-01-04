package at.tuwien.endpoint;

import at.tuwien.BaseUnitTest;
import at.tuwien.api.database.query.QueryDto;
import at.tuwien.config.ReadyConfig;
import at.tuwien.exception.*;
import at.tuwien.service.QueryService;
import at.tuwien.service.impl.StoreServiceImpl;
import com.rabbitmq.client.Channel;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@Log4j2
@SpringBootTest
@ExtendWith(SpringExtension.class)
public class StoreEndpointUnitTest extends BaseUnitTest {

    @MockBean
    private Channel channel;

    @MockBean
    private ReadyConfig readyConfig;

    @Autowired
    private StoreEndpoint storeEndpoint;

    @MockBean
    private QueryService queryService;

    @MockBean
    private StoreServiceImpl storeService;

    @Test
    public void findAll_succeeds() throws QueryStoreException, DatabaseNotFoundException, ImageNotSupportedException {

        /* mock */
        when(storeService.findAll(DATABASE_1_ID))
                .thenReturn(List.of(QUERY_1));

        /* test */
        final ResponseEntity<List<QueryDto>> response = storeEndpoint.findAll(DATABASE_1_ID);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(QUERY_1_DTO, response.getBody().get(0));
    }

    @Test
    public void find_succeeds() throws QueryStoreException, QueryNotFoundException, DatabaseNotFoundException,
            ImageNotSupportedException {

        /* mock */
        when(storeService.findOne(DATABASE_1_ID, QUERY_1_ID))
                .thenReturn(QUERY_1);

        /* test */
        final ResponseEntity<QueryDto> response = storeEndpoint.find(DATABASE_1_ID, QUERY_1_ID);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(QUERY_1_DTO, response.getBody());
    }

    @Test
    public void find_notFound_fails() throws QueryStoreException, QueryNotFoundException, DatabaseNotFoundException,
            ImageNotSupportedException {

        /* mock */
        when(storeService.findOne(DATABASE_1_ID, QUERY_1_ID))
                .thenThrow(QueryNotFoundException.class);

        /* test */
        assertThrows(QueryNotFoundException.class, () -> {
            storeEndpoint.find(DATABASE_1_ID, QUERY_1_ID);
        });
    }

    @Test
    public void find_dbNotFound_fails() throws QueryStoreException, QueryNotFoundException, DatabaseNotFoundException,
            ImageNotSupportedException {

        /* mock */
        when(storeService.findOne(DATABASE_1_ID, QUERY_1_ID))
                .thenThrow(DatabaseNotFoundException.class);

        /* test */
        assertThrows(DatabaseNotFoundException.class, () -> {
            storeEndpoint.find(DATABASE_1_ID, QUERY_1_ID);
        });
    }

}
