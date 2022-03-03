package at.tuwien.endpoint;

import at.tuwien.BaseUnitTest;
import at.tuwien.api.database.query.ExecuteStatementDto;
import at.tuwien.api.database.query.QueryDto;
import at.tuwien.api.database.query.QueryResultDto;
import at.tuwien.api.database.query.SaveStatementDto;
import at.tuwien.config.ReadyConfig;
import at.tuwien.exception.*;
import at.tuwien.service.StoreService;
import at.tuwien.service.impl.QueryServiceImpl;
import lombok.extern.log4j.Log4j2;
import net.sf.jsqlparser.JSQLParserException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.sql.SQLException;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@Log4j2
@SpringBootTest
@ExtendWith(SpringExtension.class)
public class QueryEndpointUnitTest extends BaseUnitTest {

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
            DatabaseNotFoundException, ImageNotSupportedException, ContainerNotFoundException, SQLException, JSQLParserException, TableMalformedException {
        final ExecuteStatementDto request = ExecuteStatementDto.builder()
                .statement(QUERY_1_STATEMENT)
                .build();
        final QueryResultDto result = QueryResultDto.builder()
                .id(QUERY_1_ID)
                .result(List.of(Map.of("key", "value")))
                .build();
        final Instant execution = Instant.now();

        /* mock */
        //FIXME
        when(queryService.execute(CONTAINER_1_ID, DATABASE_1_ID, request, 0L, 0L))
                .thenReturn(result);
        when(storeService.insert(CONTAINER_1_ID, DATABASE_1_ID, result, request, execution))
                .thenReturn(QUERY_1);

        /* test */
        //FIXME
        final ResponseEntity<QueryResultDto> response = queryEndpoint.execute(CONTAINER_1_ID, DATABASE_1_ID, request,0L,0L);
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertEquals(result, response.getBody());
    }

    @Test
    public void execute_emptyResult_succeeds() throws TableNotFoundException, QueryStoreException,
            QueryMalformedException, DatabaseNotFoundException, ImageNotSupportedException, ContainerNotFoundException, SQLException, JSQLParserException, TableMalformedException {
        final ExecuteStatementDto request = ExecuteStatementDto.builder()
                .statement(QUERY_1_STATEMENT)
                .build();
        final QueryResultDto result = QueryResultDto.builder()
                .id(QUERY_1_ID)
                .result(List.of())
                .build();
        final Instant execution = Instant.now();

        /* mock */
        //FIXME
        when(queryService.execute(CONTAINER_1_ID, DATABASE_1_ID, request, 0L, 0L))
                .thenReturn(result);
        when(storeService.insert(CONTAINER_1_ID, DATABASE_1_ID, result, request, execution))
                .thenReturn(QUERY_1);

        /* test */
        //FIXME
        final ResponseEntity<QueryResultDto> response = queryEndpoint.execute(CONTAINER_1_ID, DATABASE_1_ID, request,0L,0L);
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertEquals(result, response.getBody());
    }

    @Test
    public void execute_tableNotFound_fails() throws TableNotFoundException, QueryMalformedException,
            DatabaseNotFoundException, ImageNotSupportedException, ContainerNotFoundException, QueryStoreException, SQLException, JSQLParserException, TableMalformedException {
        final ExecuteStatementDto request = ExecuteStatementDto.builder()
                .statement(QUERY_1_STATEMENT)
                .build();

        /* mock */
        //FIXME
        when(queryService.execute(CONTAINER_1_ID, DATABASE_1_ID, request, 0L, 0L))
                .thenThrow(TableNotFoundException.class);

        /* test */
        assertThrows(TableNotFoundException.class, () -> {
            //FIXME
            queryEndpoint.execute(CONTAINER_1_ID, DATABASE_1_ID, request,0L,0L);
        });
    }

}
