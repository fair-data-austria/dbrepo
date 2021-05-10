package at.tuwien.service;

import at.tuwien.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class ServiceTest extends BaseIntegrationTest {

    @Test
    public void findAll_succeeds() {
        fail();
    }

    @Test
    public void findById_succeeds() {
        fail();
    }

    @Test
    public void findById_notFound_fails() {
        fail();
    }

    @Test
    public void delete_succeeds() {
        fail();
    }

    @Test
    public void delete_notFound_fails() {
        fail();
    }

    @Test
    public void delete_notPostgres_fails() {
        fail();
    }

    @Test
    public void create_succeeds() {
        fail();
    }

    @Test
    public void create_notFound_fails() {
        fail();
    }
}
