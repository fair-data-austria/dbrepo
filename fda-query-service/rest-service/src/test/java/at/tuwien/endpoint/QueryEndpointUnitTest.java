package at.tuwien.endpoint;

import at.tuwien.BaseUnitTest;
import at.tuwien.api.database.query.ExecuteQueryDto;
import at.tuwien.api.database.query.QueryDto;
import at.tuwien.api.database.query.QueryResultDto;
import at.tuwien.config.ReadyConfig;
import at.tuwien.exception.*;
import at.tuwien.service.QueryService;
import at.tuwien.service.StoreService;
import at.tuwien.service.impl.QueryServiceImpl;
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
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@Log4j2
@SpringBootTest
@ExtendWith(SpringExtension.class)
public class QueryEndpointUnitTest extends BaseUnitTest {

    @MockBean
    private Channel channel;

    @MockBean
    private ReadyConfig readyConfig;

    @Autowired
    private QueryEndpoint queryEndpoint;

    @MockBean
    private QueryServiceImpl queryService;

    @MockBean
    private StoreService storeService;

    @Test
    public void execute_succeeds() throws TableNotFoundException, QueryStoreException, QueryMalformedException,
            DatabaseNotFoundException, ImageNotSupportedException {
        final ExecuteQueryDto request = ExecuteQueryDto.builder()
                .title(QUERY_1_TITLE)
                .description(QUERY_1_DESCRIPTION)
                .query(QUERY_1_STATEMENT)
                .build();
        final QueryResultDto result = QueryResultDto.builder()
                .id(QUERY_1_ID)
                .result(List.of(Map.of("key", "value")))
                .build();
        final QueryDto query = QueryDto.builder()
                .query(QUERY_1_STATEMENT)
                .doi(QUERY_1_DOI)
                .resultHash(QUERY_1_RESULT_HASH)
                .description(QUERY_1_DESCRIPTION)
                .title(QUERY_1_TITLE)
                .created(QUERY_1_CREATED)
                .executionTimestamp(QUERY_1_EXECUTION)
                .build();

        /* mock */
        when(queryService.execute(DATABASE_1_ID, TABLE_1_ID, request))
                .thenReturn(result);
        when(storeService.insert(DATABASE_1_ID, result, request))
                .thenReturn(query);

        /* test */
        final ResponseEntity<QueryResultDto> response = queryEndpoint.execute(DATABASE_1_ID, TABLE_1_ID, request);
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertEquals(result, response.getBody());
    }

    @Test
    public void execute_emptyResult_succeeds() throws TableNotFoundException, QueryStoreException, QueryMalformedException,
            DatabaseNotFoundException, ImageNotSupportedException {
        final ExecuteQueryDto request = ExecuteQueryDto.builder()
                .title(QUERY_1_TITLE)
                .description(QUERY_1_DESCRIPTION)
                .query(QUERY_1_STATEMENT)
                .build();
        final QueryResultDto result = QueryResultDto.builder()
                .id(QUERY_1_ID)
                .result(List.of())
                .build();
        final QueryDto query = QueryDto.builder()
                .query(QUERY_1_STATEMENT)
                .doi(QUERY_1_DOI)
                .resultHash(QUERY_1_RESULT_HASH)
                .description(QUERY_1_DESCRIPTION)
                .title(QUERY_1_TITLE)
                .created(QUERY_1_CREATED)
                .executionTimestamp(QUERY_1_EXECUTION)
                .build();

        /* mock */
        when(queryService.execute(DATABASE_1_ID, TABLE_1_ID, request))
                .thenReturn(result);
        when(storeService.insert(DATABASE_1_ID, result, request))
                .thenReturn(query);

        /* test */
        final ResponseEntity<QueryResultDto> response = queryEndpoint.execute(DATABASE_1_ID, TABLE_1_ID, request);
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertEquals(result, response.getBody());
    }

    @Test
    public void execute_tableNotFound_fails() throws TableNotFoundException, QueryStoreException, QueryMalformedException,
            DatabaseNotFoundException, ImageNotSupportedException {
        final ExecuteQueryDto request = ExecuteQueryDto.builder()
                .title(QUERY_1_TITLE)
                .description(QUERY_1_DESCRIPTION)
                .query(QUERY_1_STATEMENT)
                .build();

        /* mock */
        when(queryService.execute(DATABASE_1_ID, TABLE_1_ID, request))
                .thenThrow(TableNotFoundException.class);

        /* test */
        assertThrows(TableNotFoundException.class, () -> {
            queryEndpoint.execute(DATABASE_1_ID, TABLE_1_ID, request);
        });
    }

    @Test
    public void save_succeeds() throws QueryStoreException, DatabaseNotFoundException, ImageNotSupportedException {
        final ExecuteQueryDto request = ExecuteQueryDto.builder()
                .title(QUERY_1_TITLE)
                .description(QUERY_1_DESCRIPTION)
                .query(QUERY_1_STATEMENT)
                .build();
        final QueryDto query = QueryDto.builder()
                .query(QUERY_1_STATEMENT)
                .doi(QUERY_1_DOI)
                .resultHash(QUERY_1_RESULT_HASH)
                .description(QUERY_1_DESCRIPTION)
                .title(QUERY_1_TITLE)
                .created(QUERY_1_CREATED)
                .executionTimestamp(QUERY_1_EXECUTION)
                .build();

        /* mock */
        when(storeService.insert(DATABASE_1_ID, null, request))
                .thenReturn(query);

        /* test */
        final ResponseEntity<QueryDto> response = queryEndpoint.save(DATABASE_1_ID, TABLE_1_ID, request);
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertEquals(query, response.getBody());
    }

    @Test
    public void save_dbNotFound_fails() throws QueryStoreException, DatabaseNotFoundException, ImageNotSupportedException {
        final ExecuteQueryDto request = ExecuteQueryDto.builder()
                .title(QUERY_1_TITLE)
                .description(QUERY_1_DESCRIPTION)
                .query(QUERY_1_STATEMENT)
                .build();

        /* mock */
        when(storeService.insert(DATABASE_1_ID, null, request))
                .thenThrow(DatabaseNotFoundException.class);

        /* test */
        assertThrows(DatabaseNotFoundException.class, () -> {
            queryEndpoint.save(DATABASE_1_ID, TABLE_1_ID, request);
        });
    }

    @Test
    public void reExecute_succeeds() throws TableNotFoundException, QueryStoreException, QueryMalformedException,
            DatabaseNotFoundException, ImageNotSupportedException, QueryNotFoundException {
        final QueryResultDto result = QueryResultDto.builder()
                .id(QUERY_1_ID)
                .result(List.of(Map.of("key", "value")))
                .build();
        final QueryDto query = QueryDto.builder()
                .query(QUERY_1_STATEMENT)
                .doi(QUERY_1_DOI)
                .resultHash(QUERY_1_RESULT_HASH)
                .description(QUERY_1_DESCRIPTION)
                .title(QUERY_1_TITLE)
                .created(QUERY_1_CREATED)
                .executionTimestamp(QUERY_1_EXECUTION)
                .build();

        /* mock */
        when(storeService.findOne(DATABASE_1_ID, QUERY_1_ID))
                .thenReturn(query);
        when(queryService.execute(DATABASE_1_ID, TABLE_1_ID, query))
                .thenReturn(result);

        /* test */
        final ResponseEntity<QueryResultDto> response = queryEndpoint.reExecute(DATABASE_1_ID, TABLE_1_ID, QUERY_1_ID);
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertEquals(result, response.getBody());
    }

}
