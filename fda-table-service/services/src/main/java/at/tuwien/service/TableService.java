package at.tuwien.service;

import at.tuwien.client.FdaQueryServiceClient;
import at.tuwien.dto.CreateTableViaCsvDTO;
import com.opencsv.CSVReader;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

@Service
public class TableService {
    private String CREATE_TABLE_STMT = "CREATE TABLE %s (%s);";
    private String INSERT_STMT = "INSERT INTO %s VALUES %s;";

    private FdaQueryServiceClient client;

    @Autowired
    public TableService(FdaQueryServiceClient client) {
        this.client = client;
    }

    public void createTableViaCsv(CreateTableViaCsvDTO dto) {
        try {
            File file = new ClassPathResource("VornamenTirol.csv").getFile();

            String records = "";
            CSVReader csvReader = new CSVReader(new FileReader(file));
            String[] values = null;
            String[] header = null;
            header = csvReader.readNext();
            String[] splittedHeader = header[0].split(";");
            String columnNames = Arrays.asList(splittedHeader).stream().collect(Collectors.joining(" varchar(255), ")) + "  varchar(255)";

            while ((values = csvReader.readNext()) != null) {
                String[] split = values[0].split(";");

                records += "(" + Arrays.asList(split).stream().map(elem -> "'" + elem + "'").collect(Collectors.joining(", ")) + "),";

            }
            records = records.substring(0, records.length() - 1);
            String createTableStmt = String.format(CREATE_TABLE_STMT, file.getName().replace(".csv", ""), columnNames);
            String insertIntoTableStmt = String.format(INSERT_STMT, file.getName().replace(".csv", ""), records);

            client.executeStatement(dto, createTableStmt);
            client.executeStatement(dto, insertIntoTableStmt);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
