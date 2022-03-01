package at.tuwien.service;

import at.tuwien.BaseUnitTest;
import at.tuwien.config.ReadyConfig;
import at.tuwien.exception.FileStorageException;
import at.tuwien.exception.ImageNotSupportedException;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.*;
import java.nio.charset.Charset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


@Log4j2
@SpringBootTest
@ExtendWith(SpringExtension.class)
public class CommaValueServiceUnitTest extends BaseUnitTest {

    @MockBean
    private ReadyConfig readyConfig;

    @Value("classpath:csv/csv_13.csv")
    private Resource resourceCsv13;

    @Value("classpath:csv/csv_14.csv")
    private Resource resourceCsv14;

    @Autowired
    private CommaValueService commaValueService;

    @BeforeEach
    public void beforeEach() {
        TABLE_1.setDatabase(DATABASE_1);
        TABLE_2.setDatabase(DATABASE_2);
    }

    @Test
    public void replace_null_succeeds() throws FileStorageException, IOException, ImageNotSupportedException {
        final String request = "/tmp/csv_13.csv";

        /* mock */
        final File output = new File(request);
        FileUtils.copyFile(resourceCsv13.getFile(), output);

        /* test */
        commaValueService.replace(TABLE_1, request);
        final List<String> response = FileUtils.readLines(output, Charset.defaultCharset());
        assertEquals(7, response.size());
        assertEquals("1,temp,", response.get(1));
        assertEquals("2,temp,35.1", response.get(2));
        assertEquals("3,temp,35.2", response.get(3));
        assertEquals("4,temp,", response.get(4));
        assertEquals("5,temp,36.7", response.get(5));
        assertEquals("6,temp,", response.get(6));
    }

    @Test
    public void replace_boolean_succeeds() throws FileStorageException, IOException, ImageNotSupportedException {
        final String request = "/tmp/csv_14.csv";

        /* mock */
        final File output = new File(request);
        FileUtils.copyFile(resourceCsv14.getFile(), output);

        /* test */
        commaValueService.replace(TABLE_4, request);
        final List<String> response = FileUtils.readLines(output, Charset.defaultCharset());
        assertEquals(7, response.size());
        assertEquals("1,temp,0", response.get(1));
        assertEquals("2,temp,0", response.get(2));
        assertEquals("3,temp,", response.get(3));
        assertEquals("4,temp,1", response.get(4));
        assertEquals("5,temp,0", response.get(5));
        assertEquals("6,temp,", response.get(6));
    }

}
