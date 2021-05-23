package at.tuwien.endpoint;

import at.tuwien.BaseUnitTest;
import at.tuwien.api.database.table.TableBriefDto;
import at.tuwien.api.database.table.TableCreateDto;
import at.tuwien.endpoints.TableEndpoint;
import at.tuwien.exception.*;
import at.tuwien.service.PostgresService;
import at.tuwien.service.TableService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class TableEndpointIntegrationTest extends BaseUnitTest {

    @Autowired
    private TableEndpoint tableEndpoint;

    @Test
    public void delete_succeeds() {

    }

    @Test
    public void delete_fails() {

    }

    @Test
    public void create_succeeds() {

    }

    @Test
    public void create_fails() {

    }
}
