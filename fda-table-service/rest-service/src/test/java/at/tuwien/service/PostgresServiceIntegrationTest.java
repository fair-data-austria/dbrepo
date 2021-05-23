package at.tuwien.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class PostgresServiceIntegrationTest {

    @Autowired
    private TableService tableService;

    @Test
    public void createTable_succeeds() {

    }

    @Test
    public void createTable_fails() {

    }

    @Test
    public void insertIntoTable_succeeds() {

    }

    @Test
    public void insertIntoTable_noConnection_fails() {

    }

    @Test
    public void insertIntoTable_noSql_fails() {

    }

    @Test
    public void getAllRows_succeeds() {

    }

    @Test
    public void getAllRows_noConnection_fails() {

    }

    @Test
    public void getAllRows_noSql_fails() {

    }

    @Test
    public void getCreateTableStatement_succeeds() {

    }

    @Test
    public void getCreateTableStatement_noSql_fails() {

    }

    @Test
    public void inserStatement_succeeds() {

    }

    @Test
    public void deleteTable_succeeds() {

    }

    @Test
    public void deleteTable_noConnection_fails() {

    }

    @Test
    public void deleteTable_noSql_fails() {

    }

    @Test
    public void getDeleteStatement_succeeds() {

    }

    @Test
    public void getDeleteStatement_fails() {

    }

}
