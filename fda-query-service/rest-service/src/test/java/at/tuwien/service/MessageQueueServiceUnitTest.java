package at.tuwien.service;

import at.tuwien.BaseUnitTest;
import at.tuwien.api.database.table.TableCsvDto;
import at.tuwien.config.ReadyConfig;
import at.tuwien.exception.*;
import com.rabbitmq.client.Channel;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@Log4j2
public class MessageQueueServiceUnitTest extends BaseUnitTest {

    @MockBean
    private ReadyConfig readyConfig;

    @MockBean
    private Channel channel;

    @MockBean
    private QueryService queryService;

    @Autowired
    private MessageQueueService messageQueueService;

    @BeforeEach
    public void beforeEach() {
        TABLE_1.setDatabase(DATABASE_1);
        TABLE_2.setDatabase(DATABASE_2);
    }

    @Test
    public void createUserConsumer_imageNotSupported_succeeds() throws IOException, TableNotFoundException, TableMalformedException,
            DatabaseNotFoundException, ImageNotSupportedException, ContainerNotFoundException {

        /* mock */
        doThrow(ImageNotSupportedException.class)
                .when(queryService)
                .insert(eq(CONTAINER_1_ID), eq(DATABASE_1_ID), eq(TABLE_1_ID), any(TableCsvDto.class));

        /* test */
        messageQueueService.createUserConsumer(TABLE_1);
    }

    @Test
    public void createUserConsumer_tableFailed_succeeds() throws IOException, TableNotFoundException, TableMalformedException,
            DatabaseNotFoundException, ImageNotSupportedException, ContainerNotFoundException {

        /* mock */
        doThrow(TableMalformedException.class)
                .when(queryService)
                .insert(eq(CONTAINER_1_ID), eq(DATABASE_1_ID), eq(TABLE_1_ID), any(TableCsvDto.class));

        /* test */
        messageQueueService.createUserConsumer(TABLE_1);
    }

    @Test
    public void createUserConsumer_dbNotFound_succeeds() throws IOException, TableNotFoundException, TableMalformedException,
            DatabaseNotFoundException, ImageNotSupportedException, ContainerNotFoundException {

        /* mock */
        doThrow(DatabaseNotFoundException.class)
                .when(queryService)
                .insert(eq(CONTAINER_1_ID), eq(DATABASE_1_ID), eq(TABLE_1_ID), any(TableCsvDto.class));

        /* test */
        messageQueueService.createUserConsumer(TABLE_1);
    }

    @Test
    public void createUserConsumer_tableNotFound_succeeds() throws IOException, TableNotFoundException, TableMalformedException,
            DatabaseNotFoundException, ImageNotSupportedException, ContainerNotFoundException {

        /* mock */
        doThrow(TableNotFoundException.class)
                .when(queryService)
                .insert(eq(CONTAINER_1_ID), eq(DATABASE_1_ID), eq(TABLE_1_ID), any(TableCsvDto.class));

        /* test */
        messageQueueService.createUserConsumer(TABLE_1);
    }

}
