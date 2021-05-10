package at.tuwien.service;

import at.tuwien.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class PostgresTest extends BaseIntegrationTest {

    @Test
    public void create_succeeds() {
        fail();
    }

    @Test
    public void create_noConnection_fails() {
        fail();
    }

    @Test
    public void create_invalidSyntax_fails() {
        fail();
    }

    @Test
    public void delete_succeeds() {
        fail();
    }

    @Test
    public void delete_noConnection_fails() {
        fail();
    }

    @Test
    public void delete_invalidSyntax_fails() {
        fail();
    }

    @Test
    public void getCreateStatement_succeeds() {
        fail();
    }

    @Test
    public void getDeleteStatement_succeeds() {
        fail();
    }
}
